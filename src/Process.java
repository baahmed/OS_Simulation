import java.util.LinkedList;

public class Process extends Thread{
	
	public int processID;
    ProcessState status = ProcessState.New;


    public Process(int m) {
        processID = m;
    }

    @Override
    public void run() {
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

    private void process1() {
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        OperatingSystem.getInputSemaphore().semWait(this, "input");
        OperatingSystem.printText("Enter File Name: ");
        OperatingSystem.printText(OperatingSystem.readFile(OperatingSystem.TakeInput()));
        OperatingSystem.getPrintSemaphore().semPost(this.processID, "print");
        OperatingSystem.getInputSemaphore().semPost(this.processID, "input");

        //TODO: REMOVE COMMENTS!
        setProcessState(this, ProcessState.Terminated);
    }

    private void process2() {
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        OperatingSystem.getInputSemaphore().semWait(this, "input");
        OperatingSystem.printText("Enter File Name: ");
        String filename = OperatingSystem.TakeInput();
        OperatingSystem.printText("Enter Data: ");
        String data = OperatingSystem.TakeInput();
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        OperatingSystem.getInputSemaphore().semPost(getProcessID(), "input");
        OperatingSystem.getWriteSemaphore().semWait(this, "write");
        OperatingSystem.writefile(filename, data);
        OperatingSystem.getWriteSemaphore().semPost(getProcessID(), "write");
        setProcessState(this, ProcessState.Terminated);
    }

    private void process3() {
        int x = 0;
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        while (x < 301) {
            OperatingSystem.printText(x + "\n");
            x++;
        }
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
       setProcessState(this, ProcessState.Terminated);
    }

    private void process4() {
        int x = 500;
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        while (x < 1001) {
            OperatingSystem.printText(x + "\n");
            x++;
        }
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        setProcessState(this, ProcessState.Terminated);
    }

    private void process5() {
        OperatingSystem.getPrintSemaphore().semWait(this, "print");
        OperatingSystem.getInputSemaphore().semWait(this, "input");
        OperatingSystem.printText("Enter LowerBound: ");
        String lower = OperatingSystem.TakeInput();
        OperatingSystem.printText("Enter UpperBound: ");
        String upper = OperatingSystem.TakeInput();
        OperatingSystem.getPrintSemaphore().semPost(getProcessID(), "print");
        OperatingSystem.getInputSemaphore().semPost(getProcessID(), "input");
        int lowernbr = Integer.parseInt(lower);
        int uppernbr = Integer.parseInt(upper);
        String data = "";

        while (lowernbr <= uppernbr) {
            data += lowernbr++ + "\n";
        }
        OperatingSystem.getWriteSemaphore().semWait(this, "write");
        OperatingSystem.writefile("P5.txt", data);
        OperatingSystem.getWriteSemaphore().semPost(getProcessID(), "write");
        setProcessState(this, ProcessState.Terminated);
    }

    public static void setProcessState(Process p, ProcessState s) {
        p.status = s;
        if (s == ProcessState.Terminated) {
            OperatingSystem.ProcessTable.remove(OperatingSystem.ProcessTable.indexOf(p));
        }
    }

    public static ProcessState getProcessState(Process p) {
        return p.status;
    }

    //made to be able to check the process ID.
    public int getProcessID() {
        return processID;
    }


    public static void main(String[] args) {
        LinkedList<Integer> x = new LinkedList();
        System.out.println(x.isEmpty());
    }

}
