
public class BinarySemaphore {
	
	/*
	 * use mutex if this resource is used by a process and released by the same process.
	 * use binary semaphores for conditioned use of the resource. Since both are allowed, I'm using this
	 */
	
	private int value; // the value of the mutex instance - free (1) or not free(0)?
	public static Process freeThisProcess = null; //this is the process that will be freed - according to the scheduler
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
		if(value==0) {
			//make it blocked
			Process.setProcessState(p, ProcessState.Waiting);
			
			//add to blocked queue
			switch(rsc) {
				case "read": OperatingSystem.getBlockedReadQueue().addLast(p); break;
				case "write":  OperatingSystem.getBlockedWriteQueue().addLast(p); break;
				case "print":  OperatingSystem.getBlockedPrintQueue().addLast(p); break;
				case "input":  OperatingSystem.getBlockedInputQueue().addLast(p); break;
				default: System.out.println("unsupported kernel command"); break;
			 
			}
			
			
			//semWait keeps the process busy as long as it's not allowed to claim the resource.
			while(value ==0 || value ==1) {
				//if someone released the resource, and the process is the one to be freed, 
				//then exit the busy wait.
				if(value ==1 && p == freeThisProcess) { //TODO: (need to confirm) reference check - IDs MAY be repeated!
					//make it ready and add to ready queue
					p.setProcessState(p, ProcessState.Ready);
					OperatingSystem.getReadyQueue().addLast(p);
					break;
				}
				
				//else if either the value remains 0 OR the value was 1 but it was not the process to be freed..
				//then stay in busy wait!
			}
			value = 0; //take the resource, the process is now ready with it until it's called to execute.
			p.suspend(); //TODO: we resume it ONLY when we execute!!
		}
		
		//if resource was available 
		else {
			value = 0;
		}
	}
	
	public void semPost(int id, String rsc) {
		
		Process freeProcess = new Process(1); //initalize b ay batee5 i dont care
		
		//which process is being freed? we need to know which queue to take a process from
		switch(rsc) {
		case "read": freeProcess = (Process) OperatingSystem.getBlockedReadQueue().removeFirst(); break;
		case "write": freeProcess = (Process) OperatingSystem.getBlockedWriteQueue().removeFirst(); break;
		case "print": freeProcess = (Process) OperatingSystem.getBlockedPrintQueue().removeFirst(); break;
		case "input": freeProcess = (Process) OperatingSystem.getBlockedInputQueue().removeFirst(); break;
		default: System.out.println("unsupported kernel command"); return; 
		}
		
		
		setFree(freeProcess);
		
		value = 1;
		
	}

}

