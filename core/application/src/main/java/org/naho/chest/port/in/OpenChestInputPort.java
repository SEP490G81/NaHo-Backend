package org.naho.chest.port.in;

import org.naho.chest.command.OpenChestCommand;

public interface OpenChestInputPort {
    void openChest(OpenChestCommand command);
}
