package com.semaphore.java;

import com.operatingsystem.java.OperatingSystem;
import com.process.java.Process;
import com.process.java.ProcessState;
import com.scheduler.java.Scheduler;

import java.util.Queue;

public class BinarySemaphore {

    private volatile SemaphoreState state;
    private volatile Type type;

    public BinarySemaphore(Type type) {
        this.state = SemaphoreState.ACCESSIBLE;
        this.type = type;
    }

    private void setState(SemaphoreState state) {
        this.state = state;
    }

    private SemaphoreState getState() {
        return state;
    }

    private Type getType() {
        return type;
    }

    public void semWait(Process process) {
        if (getState() == SemaphoreState.ACCESSIBLE) {
            process.setState(ProcessState.RUNNING);
            setState(SemaphoreState.INACCESSIBLE);
            return;
        }
        process.setState(ProcessState.WAITING);
        switch (getType().toString()) {
            case "READ":
                OperatingSystem.getBlockedReadQueue().add(process);
                break;
            case "WRITE":
                OperatingSystem.getBlockedWriteQueue().add(process);
                break;
            case "PRINT":
                OperatingSystem.getBlockedPrintQueue().add(process);
                break;
            case "INPUT":
                OperatingSystem.getBlockedInputQueue().add(process);
                break;
            default:
                System.out.println("unsupported kernel command");
                break;
        }
        process.suspend();
    }

    public void semPost() {
        Queue<Process> queue = null;
        switch (getType().toString()) {
            case "READ":
                queue = OperatingSystem.getBlockedReadQueue();
                break;
            case "WRITE":
                queue = OperatingSystem.getBlockedWriteQueue();
                break;
            case "PRINT":
                queue = OperatingSystem.getBlockedPrintQueue();
                break;
            case "INPUT":
                queue = OperatingSystem.getBlockedInputQueue();
                break;
            default:
                System.out.println("unsupported kernel command");
                break;
        }
        if (!queue.isEmpty()) {
            Process finishedProcess = queue.remove();
            Scheduler.scheduling(finishedProcess);
            return;
        }
        setState(SemaphoreState.ACCESSIBLE);
    }
}