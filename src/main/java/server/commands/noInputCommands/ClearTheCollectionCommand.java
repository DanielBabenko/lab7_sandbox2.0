package server.commands.noInputCommands;

import server.Command;
import server.manager.HelperController;

public class ClearTheCollectionCommand implements Command {
    private HelperController helperController;

    public ClearTheCollectionCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    public void execute()
    {
        helperController.clearCollection();
    }
}
