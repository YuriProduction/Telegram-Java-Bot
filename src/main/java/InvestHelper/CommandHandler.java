package InvestHelper;

import static java.lang.System.out;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CommandHandler {

  //Управляет командами, приходящими от польователя
  public SendMessage getOutMess() {
    return outMess;
  }

  private SendMessage outMess = new SendMessage();

  public Map<Boolean, String> getCurrentCommand() {
    return currentCommand;
  }

  public void setCurrentCommand(Map<Boolean, String> currentCommand) {
    this.currentCommand = currentCommand;
  }

  private Map<Boolean, String> currentCommand = new HashMap<Boolean, String>();

  public boolean isStillExecutable() {
    return stillExecutable;
  }

  private boolean stillExecutable = false;

  public void handleFirstTextOfCommand(String command, Long chatID, Client tempClient)
      throws IOException {
    this.outMess = new SendMessage();
    outMess.setChatId(chatID.toString());
    if (Math.abs(castDateToInt(tempClient.getDate()) - castDateToInt(new Date().toString())) >= 1) {
      out.println("Date " + tempClient.getDate());
      outMess.setText("Привет! Ты не заходил в бота больше одного дня, "
          + "перед использованием посмотри, что произошло на бирже!"
          + "Для этого введи команду \"show\"");
      tempClient.setDate(new Date().toString());
      return;
    }
    if (command.equals("/start")) {
      outMess.setText(
          "Привет \uD83D\uDC4B, меня зовут Финес. Я твой личный бот-финансист \uD83D\uDCB0."
              + "\nЖми /help, если хочешь узнать на что я способен \uD83E\uDDBE");

    } else if (command.equals("/help")) {
      outMess.setText(
          "\n" +
              "1)Введите \"/show\" чтобы посмотреть цены всех акций на фондовой бирже\n" +
              "2)Введите \"/add\" чтобы добавить конкретную акцию\n" +
              "3)Введите \"/help\" чтобы получить помощь\n" +
              "4)Введите \"/statistic\" чтобы получить статистику по текущим инвестицям\n"
      );
    } else if (command.equals("/add")) {
      outMess.setText("Введите команию, акции которой вы хотите приобрести");
    } else if (command.equals("/show")) {
      outMess.setText("Начинаем загрузку данных с биржи...Немного подождите");
      stillExecutable = true;
    } else if (command.equals("/statistic")) {
      String startMes = "Перед каждым выводом "
          + "статистики рекомендуем вводить команду \"/show\", "
          + "чтобы данные были наиболее актуальными!\n\n";
      String stat = statisticComputer.computeStatistics(tempClient.getInvestmentPortfolio(),parserStocks.getQuotes());
      outMess.setText(startMes + stat);

    } else {
      outMess.setText("Сообщение не распознано");
    }
  }

  private final StatisticComputer statisticComputer = new StatisticComputer();
  private final ParserStocks parserStocks = new ParserStocks();

  public String stillExecutableMethodForQuotesReturn() throws IOException {
    stillExecutable = false;
    parserStocks.parseQuotesData();
    return parserStocks.getTextForUserAboutQuotes().toString();
  }

  private boolean nameOfcompanyIsSentByUser = false;

  private String nameOfCompany = "Error company";

  public void doCommandLogic(String command, String textOfMessage, Long chatID, Client tempClient) {
    this.outMess = new SendMessage();

    outMess.setChatId(chatID.toString());
    if (command.equals("/add")) {
      if (!nameOfcompanyIsSentByUser) {
        nameOfCompany = textOfMessage;
        if (parserStocks.existsCompany(nameOfCompany)) {
          outMess.setText("Введите количество акций, которое вы желаете приобрести");
        } else {
          outMess.setText("Такой фирме на рынке нет или вы давно"
              + " не смотрели текущую ситуацию на бирже. "
              + "Выберите другую компанию "
              + "или нажмите \"/show\" и попробуйте еще раз");
          return;
        }

        nameOfcompanyIsSentByUser = true;
      } else {

        int countOfStocks = Integer.parseInt(textOfMessage);
        tempClient.addStockToInvestPortfolio(nameOfCompany, countOfStocks,
            parserStocks.getPriceOfStock(nameOfCompany));
        nameOfcompanyIsSentByUser = false;
        currentCommand.put(true, "Default command");//ставим дефолтную команду
      }
    } else {//(Default command,/help,/start) //если команды выполнены, а пользователь что-то пишет
      outMess.setText("Вся логика выполнена. Команды перед вами. Делайте что хотите");
    }
  }

  public boolean isCommand(String argum) {
    for (String x : commandslist) {
      if (argum.equals(x)) {
        return true;
      }
    }
    return false;
  }

  private final String[] commandslist = new String[]{"/add", "/start",
      "/help", "/show", "/statistic"};

  private static int castDateToInt(String data) {
    char[] charData = data.toCharArray();
    String strDay = String.valueOf(charData[8]) + charData[9];
    return Integer.parseInt(strDay);
  }

}
