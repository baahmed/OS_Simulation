package com.scheduler.java;

import com.operatingsystem.java.OperatingSystem;
import com.process.java.Process;

public class Scheduler {

    private static Process currentProcess = null;

    public static Process getCurrentProcess() {
        return currentProcess;
    }

    public static void run() {
        if ((getCurrentProcess() == null ||
                getCurrentProcess().getProcessState().toString().equals(Process.State.TERMINATED.toString()))
                && !OperatingSystem.getReadyQueue().isEmpty()) {
            currentProcess = OperatingSystem.getReadyQueue().remove();
            if (currentProcess.isAlive()) {
                currentProcess.notify();
                return;
            }
            currentProcess.start();
        }
    }

    public static void scheduling(Process process) {
        OperatingSystem.getReadyQueue().add(process);
        run();
    }
}