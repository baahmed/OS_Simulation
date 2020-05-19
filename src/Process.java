import java.util.LinkedList;


public class Process extends Thread{
	
	public int processID;
    ProcessState status = ProcessState.New;


    public Process(int m) {
        processID = m;
    }

    @Override
    public void run() {
    	setProcessState(this, ProcessState.Running);
        switch (processID) {
            case 1:
                process1();
                break;
            case 2:
                process2();
                break;
            case 3:
                process3();
                break;
            case 4:
                process4();
                break;
            case 5:
                process5();
                break;
        }
    }
    
    /*
     * semaphore usage integrated!
     */

    private void process1() {
    	
    	/*
    	 * Since process 1 prints to prompt the user to enter a
    	 * file name, takes an input (the name), and reads the file -
    	 * then it semWaits those resources and together to not have
    	 * some unexpected "cuts" in the execution.
    	 */
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        OperatingSystem.getInputSemaphore().semWait(this, "input");
        OperatingSystem.getReadSemaphore().semWait(this,"read");
        
        OperatingSystem.printText("Enter File Name: ");
        OperatingSystem.printText(OperatingSystem.readFile(OperatingSystem.TakeInput()));
        
        
        /*
         * once the process is done with the resources, it posts them.
         */
        OperatingSystem.getPrintSemaphore().semPost(this.processID, "print");
        OperatingSystem.getInputSemaphore().semPost(this.processID, "input");
        OperatingSystem.getReadSemaphore().semPost(this.processID, "read");
        
        /*
         * process complete.
         */
        setProcessState(this, ProcessState.Terminated);
    }

    
    
    
    private void process2() {
    	/*
    	 * Since process 2 prints a message to prompt the user 
    	 * to enter some inputs, and takes those inputs, then writes,
    	 * then we need to semWait those three resources. We must
    	 * do print and input together to not have some weird "jumps"
    	 * when executing the process so that we ensure the prompt and input
    	 * are done at the same time as is expected.
    	 */
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        OperatingSystem.getInputSemaphore().semWait(this, "input");
        
        OperatingSystem.printText("Enter File Name: ");
        String filename = OperatingSystem.TakeInput();
        OperatingSystem.printText("Enter Data: ");
        String data = OperatingSystem.TakeInput();
        
        
        
        /*
         * we release the print and input semaphores - we are done with the user
         * prompt.
         */
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        OperatingSystem.getInputSemaphore().semPost(getProcessID(), "input");
        
        
        
        /*
         * since doing writing is not gonna be an issue in the incoherent input
         * prompt for the user, we take the semaphore and release it after writing that
         * data later. 
         */
        OperatingSystem.getWriteSemaphore().semWait(this, "write");
        OperatingSystem.writefile(filename, data);
        OperatingSystem.getWriteSemaphore().semPost(getProcessID(), "write");
        
        
        
        /*
         * Process complete.
         */
        setProcessState(this, ProcessState.Terminated);
    }

    
    
    
    private void process3() {
        int x = 0;
        
        /*
         * since this process only prints numbers, then it will first 
         * claim the print semaphore, then print ALL the numbers, THEN
         * release the resource. We sempost after all numbers are printed 
         * because we don't want "cuts" in the printing as opposed to
         * milestone I.
         */
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        while (x < 301) {
            OperatingSystem.printText(x + "\n");
            x++;
        }
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        
        /*
         * Process complete.
         */
        setProcessState(this, ProcessState.Terminated);
    }

    private void process4() {
        int x = 500;
        /*
         * since this process only prints numbers, then it will first 
         * claim the print semaphore, then print ALL the numbers, THEN
         * release the resource. We sempost after all numbers are printed 
         * because we don't want "cuts" in the printing as opposed to
         * milestone I.
         */
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        while (x < 1001) {
            OperatingSystem.printText(x + "\n");
            x++;
        }
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        
        /*
         * Process complete.
         */
        setProcessState(this, ProcessState.Terminated);
    }

    private void process5() {
    	
    	/*
    	 * Since process 5 prints a message to prompt the user 
    	 * to enter some inputs, and takes those inputs, then writes,
    	 * then we need to semWait those three resources. We must
    	 * do print and input together to not have some weird "jumps"
    	 * when executing the process (as opposed to Milestone I)
    	 *  so that we ensure the prompt and input
    	 * are done at the same time as is expected.
    	 */
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        OperatingSystem.getInputSemaphore().semWait(this, "input");
        
        
        OperatingSystem.printText("Enter LowerBound: ");
        String lower = OperatingSystem.TakeInput();
        OperatingSystem.printText("Enter UpperBound: ");
        String upper = OperatingSystem.TakeInput();
        
        
        
        
        /*
         * we release the print and input semaphores - we are done with the user
         * prompt.
         */
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        OperatingSystem.getInputSemaphore().semPost(getProcessID(), "input");
        
        /*
         * for this part, no critical sections are accessed, so no need for 
         * semaphores.
         */
        
        int lowernbr = Integer.parseInt(lower);
        int uppernbr = Integer.parseInt(upper);
        String data = "";

        while (lowernbr <= uppernbr) {
            data += lowernbr++ + "\n";
        }
        
        
        /*
         * once again we access a critical resource. We want to write to a file.
         * So we take the write semaphore, write the file, then release the 
         * semaphore.
         */
        OperatingSystem.getWriteSemaphore().semWait(this, "write");
        
        OperatingSystem.writefile("P5.txt", data);
        
        OperatingSystem.getWriteSemaphore().semPost(getProcessID(), "write");
        
        
        
        /*
         * Process complete.
         */
        setProcessState(this, ProcessState.Terminated);
    }

    public static void setProcessState(Process p, ProcessState s) {
        p.status = s;
        if (s == ProcessState.Terminated) {
            OperatingSystem.ProcessTable.remove(OperatingSystem.ProcessTable.indexOf(p));
          
            //TODO: comment out when testing sempahores only
            OperatingSystem.schedule();
        }
    }

    public static ProcessState getProcessState(Process p) {
        return p.status;
    }

    public int getProcessID() {
        return processID;
    }


    public static void main(String[] args) {
        LinkedList<Integer> x = new LinkedList();
        System.out.println(x.isEmpty());
    }


}
