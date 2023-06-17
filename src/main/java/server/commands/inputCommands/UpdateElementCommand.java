package server.commands.inputCommands;


import server.manager.HelperController;
import server.UpdateCommand;

import java.io.IOException;
import java.text.ParseException;

//все команды наследуются от 4 интерфейсов: в основном это Command, реже - ElementCommand, IDCommand и UpdateCommand.
// В данном случае имплементируем UpdateCommand.

public class UpdateElementCommand implements UpdateCommand {
    private HelperController helperController;

    //создаёи команду, котораыя будет действовать на заданный корневой объект
    public UpdateElementCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    //переопределяем execute по схеме: root.[нужный нам метод]
    // Если у команды есть входные данные, не забываем про исключения.
    @Override
    public void execute(int id) {
        try {
            helperController.update(id);
        } catch (IOException | ParseException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

