
public class BinarySemaphore {
	  /*
     * use mutex if this resource is used by a process and released by the same process.
     * use binary semaphores for conditioned use of the resource. Since both are allowed, I'm using this
     */

    private volatile int value; // the value of the mutex instance - free (1) or not free(0)?
    public volatile Process freeThisProcess = null; //this is the process that will be freed - according to the scheduler
    //TODO: how to initialize it?


    public BinarySemaphore() {
        this.value = 1; //initially, the resource is available
    }

    public void setFree(Process P) { //scheduler uses it to pick the thread it will free
        freeThisProcess = P;
    }

    /*
     * process to add and remove it from queues, and rsc to
     */
    public void semWait(Process p, String rsc) {
        //if resource was not available
        if (value == 0) {
            //make it blocked
        	//TODO: remove
        	System.out.println("FOR TRACING: Process " + p.getProcessID() + " is now blocked");
            Process.setProcessState(p, ProcessState.Waiting);
            //add to blocked queue
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
            //semWait keeps the process busy as long as it's not allowed to claim the resource.
            while (value == 0) {
                //if someone released the resource, and the process is the one to be freed,
                //then exit the busy wait.
                if (p == freeThisProcess) { //TODO: (need to confirm) reference check - IDs MAY be repeated!
                	System.out.println("FOR TRACING: now exit busy wait");
                    break;
                }


                //else if either the value remains 0 OR it was not the process to be freed..
                //then stay in busy wait!
            }
            value = 0; //take the resource, the process is now ready with it until it's called to execute.
            
            
            //TODO: MUST BE SUSPEND
            p.suspend(); //TODO: we resume it ONLY when we execute!!
        }
        //if resource was available
        else {
            value = 0;
        }
    }

    public void semPost(int id, String rsc) {
        Process freeProcess = null;
        //which process is being freed? we need to know which queue to take a process from
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
        //make it ready and add to ready queue
        if (!(freeProcess == null)) {
            Process.setProcessState(freeProcess, ProcessState.Ready);
            OperatingSystem.getReadyQueue().add(freeProcess);
            setFree(freeProcess); //do not set semaphore to 1 - just free a blocked process (it took the rsc already)
            //TODO: comment next line
            //System.out.println("process " + freeProcess.getProcessID() + " is now ready");
            
        } else {
            //else if nothing to be free, make it 1 for anyone to take.
            value = 1;
        }
    }
}
