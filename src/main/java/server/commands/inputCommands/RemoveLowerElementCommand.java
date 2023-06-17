package server.commands.inputCommands;


import server.Command;
import server.manager.HelperController;

import java.io.IOException;

public class RemoveLowerElementCommand implements Command {
    private HelperController helperController;

    public RemoveLowerElementCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void execute() {
        try {
            helperController.removeLower();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
