package org.naho.chest.port.in;

import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.result.OpenChestResult;

public interface OpenChestInputPort {
    OpenChestResult openChest(OpenChestCommand command);
}
