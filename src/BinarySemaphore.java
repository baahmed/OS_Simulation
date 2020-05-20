  /*
     * Use mutex if this resource should be used by a process and released by the same process.
     * Use binary semaphores for conditioned use of the resource - that is, only one process 
     * could use it at a time and it doesn't matter
     * that the process that is using it is the process that is freeing it; it could pose 
     * risks in some systems. 
     * Since both implementations are allowed, I am using Binary Semaphore in this simulation.
     */

public class BinarySemaphore {
	
	/*
	 * the value of the BinarySemaphore instance - free (1) or not free(0)?
	 * We use the volatile keyword to make sure any assignment is written 
	 * directly to main memory to ensure all threads are seeing the same value.
	 * Typically changes are cached thread-locally first, so by the time it gets 
	 * written to memory, it could be too late since other threads would have read the old 
	 * value from memory already. They should all see the same availability for the 
	 * resource.
	 */
    private volatile int value; 
    
    
    
    /* 
     * Since we could have several blocked processes waiting for the resource, and the 
     * binary semaphore can release the resource to only one process, then we must select
     * which of the blocked processes to release the resource too. 
     * 
     * In our code, that process is the
     * one that is first in the blocked queue. This pointer tracks that process to grant
     * the resource to as soon as it's released by the previous process using it.
     */
    public volatile Process freeThisProcess = null; 
  

    
    /*
     * constructor of the semaphore instance.
     */
    public BinarySemaphore() {
    	/*
    	 * initially, the resource is available for use. The assumption here is that 
    	 * semaphores are used with the critical resource they govern right away - 
    	 * so at the start state, that resource is available.
    	 */
        this.value = 1; 
    }
    
    
    
    public void setFree(Process P) { 
    	/*
    	 * updates the process to be freed for all instances. This method basically
    	 * announces to all the waiting processes which one will be freed next.
    	 * The process whose reference matches the announced one will be released 
    	 * from the blocked queue and granted the resource.
    	 */
        freeThisProcess = P;
    }

    
    
    
    
    /*
     * the semWait method of the semaphore is called on by some process that 
     * wishes to use the associated resource. It is either granted the resource if 
     * the resource were available or becomes blocked, waiting for the resource
     * to be available for it.
     * 
     * The two parameters taken are the process that wants to take the resource 
     * and what resource it wishes to take. Could have done four instances/classes for
     * each resource instead - but don't repeat yourself principles! :-)
     */
    public void semWait(Process p, String rsc) {
        /*if resource was not available...
         */
        if (value == 0) {

        	/*
        	 * first of all, change the process state to waiting (blocked).
        	 */
            Process.setProcessState(p, ProcessState.Waiting);
        	//TODO: this was left for tracing purposes
        	System.out.println("FOR TRACING: Process " + p.getProcessID() + " gets blocked. STATE: " +  Process.getProcessState(p));

        	
        	
        	
            /*
             * now that the process is blocked, it must be added to its 
             * corresponding queue - the blocked queue - to keep track
             * of the processes that are waiting for the associated resource.
             * Each of the resources has its own blocked queue so we know
             * which proccesses are the ones needing some resource.
             */
            switch (rsc) {
                case "read":
                    OperatingSystem.getBlockedReadQueue().add(p);
                    break;
                case "write":
                    OperatingSystem.getBlockedWriteQueue().add(p);
                    break;
                case "print":
                    OperatingSystem.getBlockedPrintQueue().add(p);
                    break;
                case "input":
                    OperatingSystem.getBlockedInputQueue().add(p);
                    break;
                default:
                    System.out.println("unsupported kernel command");
                    break;

            }
            
            
            
            /*
             * we keep the process in busy wait as long as it's not 
             * allowed to claim the resource. It busy waits as long 
             * as long as the resource is not available. So all our
             * blocked processes are busy waiting. If the process was 
             * selected to be freed, then freeThisProcess would carry
             * the ID of that process. So all processes keep checking 
             * if their ID is that of the process to claim the resource.
             * If that's the case, the process calims the resouce and becomes
             * unblocked, now going back to the ready state until the 
             * scheduler tells it to resume where it stopped.
             */
            while (value == 0) {
                //if someone released the resource, and the process is the one to be freed,
                //then exit the busy wait.
            	
            	
            	
            	/*IDs MAY be repeated!
            	 * we might have several instances with the same process number.
            	 * That's why I compare by reference. No two processes can share
            	 * the same memory address pointer.
            	 */
                if (p == freeThisProcess) { 
                	//TODO: this was left for tracing purposes
                	System.out.println("FOR TRACING: Process " + p.getProcessID() + " exits busy wait and is now ready. STATE: " +  Process.getProcessState(p));
                	
                	//exit the busy wait.
                    break;
                }


                /*else if either the value remains 0 
                 * OR it was not the process to be freed..
                 * then stay in busy wait!
                 */
                
            }
            /*
             * if the process has reached this point,
             * then it was the chosen one to claim the resource.
             * It takes the resource. The process is now ready with it 
             * until it's selected to execute by the scheduler. Make value 0
             * in case it's not.
             */
            
            value = 0; 
            
            
            
            /*
             * suspend the process since it's now in the ready state
             * the scheduler resumes when it is chosen to execute.
             */
            
            //TODO: comment this if testing for semaphore
            //p.suspend(); 
            
            //TODO: comment this when testing semaphores only
            //OperatingSystem.schedule();
        }
        
        
        
        /*
         * the simple case in which the resouce was available (1)
         * in the first place, then simply claim it and continue 
         * as normal.
         */
        else {
            value = 0;
        }
    }
    
    
    
    /*
     * This semPost method is responsible for making the resource available
     * when released by some process. It also checks which process to make
     * the resource it freed available to, since we can't just release the 
     * resource for all the waiting processes or we might have locks and 
     * other conditions that can jeopardize the use of the resource.
     * 
     * It takes the resource to be posted, and the id (which is not used in
     * the code itself since this is not a mutex and the freeing process 
     * doesn't matter to us. I was just testing something
     * and wanted to see more details of what's going on.)
     */
    public void semPost(int id, String rsc) {
    	
    	/*
    	 * make the variable to hold the pointer of the process to be
    	 * granted the resource next.
    	 */
        Process freeProcess = null;
        
        /*
         * first of all, we need to check the resource that is being posted.
         * According to it, we will claim the first blocked process of the 
         * corresponding blocked queue of that resource.
         * 
         * If the blocked queue is empty, we return null. This means there is 
         * no blocked process waiting for the resource. I handle this below.
         */
        switch (rsc) {
            case "read":
                freeProcess = (!(OperatingSystem.getBlockedReadQueue()).isEmpty()) ? (Process) OperatingSystem.getBlockedReadQueue().poll() : null;
                break;
            case "write":
                freeProcess = (!(OperatingSystem.getBlockedWriteQueue()).isEmpty()) ? (Process) OperatingSystem.getBlockedWriteQueue().poll() : null;
                break;
            case "print":
                freeProcess = (!(OperatingSystem.getBlockedPrintQueue()).isEmpty()) ? (Process) OperatingSystem.getBlockedPrintQueue().poll() : null;
                break;
            case "input":
                freeProcess = (!(OperatingSystem.getBlockedInputQueue().isEmpty())) ? (Process) OperatingSystem.getBlockedInputQueue().poll() : null;
                break;
            default:
                System.out.println("unsupported kernel command");
                return;
        }
        
        /*
         * in case the blocked queue was NOT empty, then we need to take
         * this first blocked process's pointer and set it to freeThisProcess 
         * of the semaphore of the resource to announce to all the blocked processes
         * which to claim the resource next. The one that matches the ID of the first
         * process in the blocked queue will claim the resource and the rest will be blocked.
         * It will also become ready and be added to the ready queue since it is 
         * the one that takes the resource.
         */
        if (!(freeProcess == null)) {
            Process.setProcessState(freeProcess, ProcessState.Ready);
            OperatingSystem.getReadyQueue().add(freeProcess);
            setFree(freeProcess); 

            /*
             * we do not set semaphore to 1 - just free a blocked process instead 
             * (it took the resource already)
             */
     
            //System.out.println("process " + freeProcess.getProcessID() + " is now ready");
            
        } 
        
        
        
        /*
         * in case there is no blocked process for the resource, we then release the 
         * semaphore for any process that needs the resource to claim.
         */
        else {
            value = 1;
        }
    }
}