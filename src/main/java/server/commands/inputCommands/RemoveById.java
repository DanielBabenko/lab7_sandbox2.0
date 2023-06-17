package server.commands.inputCommands;

import server.commands.Invoker;
import server.manager.HelperController;

import java.io.IOException;

public class RemoveById extends Invoker {

    private HelperController helperController;

    private static final String COMMAND_NAME = RemoveById.class.getSimpleName();

    public RemoveById(HelperController helperController) {
        this.helperController = helperController;
    }


    public void execute(int id) throws IOException {
        helperController.removeEl(id);
    }

    public  String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void doCommand(String e) {
        try {
            int i = Integer.parseInt(e);
            execute(i);
        } catch (IOException | NumberFormatException ex) {
            try {
                helperController.getServer().sentToClient("Невалидный ввод данных. Повторите попытку.");
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }
    }
}
