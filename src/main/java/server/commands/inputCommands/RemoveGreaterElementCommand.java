package server.commands.inputCommands;


import server.Command;
import server.manager.HelperController;

import java.io.IOException;
import java.text.ParseException;

public class RemoveGreaterElementCommand implements Command {
    private HelperController helperController;

    public RemoveGreaterElementCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void execute() {
        try {
            helperController.removeGreater();
        } catch (IOException |ParseException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

