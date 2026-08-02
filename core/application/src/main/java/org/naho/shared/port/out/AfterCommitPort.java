package org.naho.shared.port.out;

public interface AfterCommitPort {
    void execute(Runnable action);
}
