package com.process.java;

import com.operatingsystem.java.OperatingSystem;

public class Process extends Thread {

    private int processID;
    private ProcessState state;


    public Process(int m) {
        processID = m;
        state = ProcessState.NEW;
    }

    public void setState(ProcessState s) {
        state = s;
        if (s == ProcessState.TERMINATED) {
            OperatingSystem.terminateWithScheduling(this);
        }
    }

    public ProcessState getProcessState() {
        return state;
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
        OperatingSystem.getPrintSemaphore().semWait(this);
        OperatingSystem.getInputSemaphore().semWait(this);
        OperatingSystem.getReadSemaphore().semWait(this);
        OperatingSystem.printText("Enter File Name: ");
        OperatingSystem.printText(OperatingSystem.readFile(OperatingSystem.TakeInput()));
        OperatingSystem.getPrintSemaphore().semPost();
        OperatingSystem.getInputSemaphore().semPost();
        OperatingSystem.getReadSemaphore().semPost();

        setState(ProcessState.TERMINATED);
    }

    private void process2() {
        OperatingSystem.getPrintSemaphore().semWait(this);
        OperatingSystem.getInputSemaphore().semWait(this);
        OperatingSystem.printText("Enter File Name: ");
        String filename = OperatingSystem.TakeInput();
        OperatingSystem.printText("Enter Data: ");
        String data = OperatingSystem.TakeInput();
        OperatingSystem.getPrintSemaphore().semPost();
        OperatingSystem.getInputSemaphore().semPost();
        OperatingSystem.getWriteSemaphore().semWait(this);
        OperatingSystem.writefile(filename, data);
        OperatingSystem.getWriteSemaphore().semPost();
        setState(ProcessState.TERMINATED);
    }

    private void process3() {
        int x = 0;
        OperatingSystem.getPrintSemaphore().semWait(this);
        while (x < 301) {
            OperatingSystem.printText(x + "\n");
            x++;
        }
        OperatingSystem.getPrintSemaphore().semPost();
        setState(ProcessState.TERMINATED);
    }

    private void process4() {
        int x = 500;
        OperatingSystem.getPrintSemaphore().semWait(this);
        while (x < 1001) {
            OperatingSystem.printText(x + "\n");
            x++;
        }
        OperatingSystem.getPrintSemaphore().semPost();
        setState(ProcessState.TERMINATED);
    }

    private void process5() {
        OperatingSystem.getPrintSemaphore().semWait(this);
        OperatingSystem.getInputSemaphore().semWait(this);
        OperatingSystem.printText("Enter LowerBound: ");
        String lower = OperatingSystem.TakeInput();
        OperatingSystem.printText("Enter UpperBound: ");
        String upper = OperatingSystem.TakeInput();
        OperatingSystem.getPrintSemaphore().semPost();
        OperatingSystem.getInputSemaphore().semPost();
        int lowernbr = Integer.parseInt(lower);
        int uppernbr = Integer.parseInt(upper);
        String data = "";

        while (lowernbr <= uppernbr) {
            data += lowernbr++ + "\n";
        }
        OperatingSystem.getWriteSemaphore().semWait(this);
        OperatingSystem.writefile("P5.txt", data);
        OperatingSystem.getWriteSemaphore().semPost();
        setState(ProcessState.TERMINATED);
    }
}