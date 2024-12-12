package com.example.powerflowcontroller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Main extends Application {
    private int logPart;
    private int N;
    private boolean autoTraining = false;
    TableView<StepData> stringgrid1;
    TextField nameOfALG;
    Label staticText4;
    ComboBox<String> numberofstep;
    private Configuration config;
    private double[] k_y = new double[20];
    private double[] coefficients = new double[20];
    private LogLine1[] log1 = new LogLine1[86401];
    private int actualStep = 0;
    private double startOfStep;
    private JSONObject jsonAlgorithm;
    private JSONArray stepsArray;
    private Socket clientSocket, powerClient, idtcpClient1;
    private ServerSocket serverSocket;
    private RadioMenuItem adminItem;
    private MenuItem connectServer;
    private MenuItem disconnectServer;
    private MenuItem clientConnect;
    private MenuItem clientDisconnect;
    MenuItem createNew;
    MenuItem saveAlg;
    MenuItem readAlg;
    MenuItem edit;
    MenuItem auto;
    MenuItem manual;
    private FileChooser fileChooser;
    private BufferedReader in;
    private PrintWriter out;
    private TextArea history;
    private TextField edit1, requestText, serverText, wrongData, clientIP, clientPort, serverPort, measureName, value;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3 = new Timer();
    private boolean isTimer1Running = false;
    private boolean isTimer2Running = false;
    private boolean isTimer3Running = false;
    private TimerTask timerTask1 = new TimerTask() {
        @Override
        public void run() {}
    };
    private TimerTask timerTask2 = new TimerTask() {
        @Override
        public void run() {}
    };
    private TimerTask timerTask3 = new TimerTask() {
        @Override
        public void run() {}
    };
    private CheckBox autoScroll;
    private Circle wrongDataCircle;
    private CheckMenuItem u1, u2, u3, u4, u5, u6, u7, u8, u9, u10;
    private CheckMenuItem i1, i2, i3, i4, i5, i6, i7, i8, i9, i10;
    ComboBox<String> change1, change2, change3;
    RadioMenuItem userItem;
    private Button sendButton, serverSend, stateInfo, logStart, refresh, addStep, deleteStep, editStep, execute;
    private ImageView image1;
    private double stepTimeCounter;
    private int voltageToSet;

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        powerClient = new Socket();
        fileChooser = new FileChooser();
        Menu menuConnection = new Menu("Connection");
        Menu server = new Menu("server");
        connectServer = new MenuItem("Connect");
        disconnectServer = new MenuItem("Disconnect");
        disconnectServer.setDisable(true);
        server.getItems().addAll(connectServer, disconnectServer);
        Menu client = new Menu("client");
        clientConnect = new MenuItem("Connect");
        clientDisconnect = new MenuItem("Disconnect");
        clientDisconnect.setDisable(true);
        client.getItems().addAll(clientConnect);
        MenuItem connectModular = new MenuItem("Connect modular");
        menuConnection.getItems().addAll(server, client, connectModular);
        Menu menuSaveRead = new Menu("Save/Read");
        MenuItem save = new MenuItem("Save");
        MenuItem read = new MenuItem("Read");
        MenuItem saveConfiguration = new MenuItem("Save configuration");
        MenuItem readConfiguration = new MenuItem("Read configuration");
        menuSaveRead.getItems().addAll(save, read, saveConfiguration, readConfiguration);
        config = new Configuration();
        Menu menuParameters = new Menu("Parameters");
        Menu uMenu = new Menu("U");
        u1 = new CheckMenuItem("U 1");
        u2 = new CheckMenuItem("U 2");
        u3 = new CheckMenuItem("U 3");
        u4 = new CheckMenuItem("U 4");
        u5 = new CheckMenuItem("U 5");
        u6 = new CheckMenuItem("U 6");
        u7 = new CheckMenuItem("U 7");
        u8 = new CheckMenuItem("U 8");
        u9 = new CheckMenuItem("U 9");
        u10 = new CheckMenuItem("U 10");
        uMenu.getItems().addAll(u1, u2, u3, u4, u5, u6, u7, u8, u9, u10);
        Menu iMenu = new Menu("I");
        i1 = new CheckMenuItem("I 1");
        i2 = new CheckMenuItem("I 2");
        i3 = new CheckMenuItem("I 3");
        i4 = new CheckMenuItem("I 4");
        i5 = new CheckMenuItem("I 5");
        i6 = new CheckMenuItem("I 6");
        i7 = new CheckMenuItem("I 7");
        i8 = new CheckMenuItem("I 8");
        i9 = new CheckMenuItem("I 9");
        i10 = new CheckMenuItem("I 10");
        iMenu.getItems().addAll(i1, i2, i3, i4, i5, i6, i7, i8, i9, i10);
        menuParameters.getItems().addAll(uMenu, iMenu);
        Menu menuScale = new Menu("Scale");
        Menu setScaleX = new Menu("Set Scale X");
        MenuItem t10m = new MenuItem("10 мин");
        MenuItem t30m = new MenuItem("30 мин");
        MenuItem t1h = new MenuItem("1 час");
        MenuItem t2h = new MenuItem("2 час");
        MenuItem t4h = new MenuItem("4 час");
        MenuItem t8h = new MenuItem("8 час");
        setScaleX.getItems().addAll(t10m, t30m, t1h, t2h, t4h, t8h);
        MenuItem setScaleY = new MenuItem("Set Scale Y");
        menuScale.getItems().addAll(setScaleX, setScaleY);
        Menu menuMode = new Menu("Mode");
        adminItem = new RadioMenuItem("Admin");
        userItem = new RadioMenuItem("User");
        ToggleGroup modeGroup = new ToggleGroup();
        adminItem.setToggleGroup(modeGroup);
        userItem.setToggleGroup(modeGroup);
        adminItem.setSelected(true);
        menuMode.getItems().addAll(adminItem, userItem);
        Menu menuAlgorithm = new Menu("Algorithm");
        createNew = new MenuItem("Create new");
        saveAlg = new MenuItem("Save");
        readAlg = new MenuItem("Read");
        edit = new MenuItem("Edit");
        Menu modeAlg = new Menu("Mode");
        auto = new MenuItem("Auto");
        manual = new MenuItem("Manual");
        modeAlg.getItems().addAll(auto, manual);
        MenuItem startStop = new MenuItem("Start/Stop");
        MenuItem restartAlg = new MenuItem("Restart algorithm");
        MenuItem numberOfSource = new MenuItem("Number of source");
        menuAlgorithm.getItems().addAll(createNew, edit, saveAlg, readAlg, modeAlg, startStop, restartAlg, numberOfSource);
        Menu menuPowerSupply = new Menu("Power Supply");
        Menu suppliers = new Menu("Suppliers");
        MenuItem one = new MenuItem("1");
        MenuItem two = new MenuItem("2");
        MenuItem three = new MenuItem("3");
        suppliers.getItems().addAll(one, two, three);
        MenuItem executePowerSupply = new MenuItem("Execute");
        menuPowerSupply.getItems().addAll(suppliers, executePowerSupply);
        menuBar.getMenus().addAll(menuConnection, menuSaveRead, menuParameters, menuScale, menuMode, menuAlgorithm, menuPowerSupply);
        measureName = new TextField();
        edit1 = new TextField("1");
        requestText = new TextField("");
        serverText = new TextField("");
        wrongData = new TextField("Некорректные данные: ");
        clientIP = new TextField("10.1.2.51");
        clientPort = new TextField("45644");
        serverPort = new TextField("45644");
        sendButton = new Button("Send");
        serverSend = new Button("Send");
        stateInfo = new Button("State info");
        logStart = new Button("Log start");
        refresh = new Button("Refresh");
        addStep = new Button("Добавить шаг");
        deleteStep = new Button("Удалить шаг");
        editStep = new Button("Изменить шаг");
        execute = new Button("Выполнить");
        autoScroll = new CheckBox("AUTOSCROLL");
        wrongDataCircle = new Circle(10, Color.MAROON);
        history = new TextArea();
        history.setEditable(false);
        Label dateStrt = new Label("56");
        HBox buttonRow = new HBox(10);
        buttonRow.getChildren().addAll(stateInfo, logStart, refresh);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(column1, column2);
        gridPane.add(new Label("Название измерения"), 0, 0);
        gridPane.add(measureName, 1, 0);
        gridPane.add(buttonRow, 0, 1, 2, 1);
        gridPane.add(new Label("Частота логгирования, с"), 0, 2);
        gridPane.add(edit1, 1, 2);
        gridPane.add(history, 0, 3, 2, 1);
        HBox ipPortRow = new HBox(10);
        ipPortRow.getChildren().addAll(clientIP, clientPort, serverPort);
        gridPane.add(ipPortRow, 0, 4, 2, 1);
        HBox sendRow = new HBox(10);
        sendRow.getChildren().addAll(sendButton, requestText, serverText, serverSend);
        gridPane.add(sendRow, 0, 5, 2, 1);
        requestText.setPrefWidth(50);
        requestText.setMaxWidth(50);
        serverText.setPrefWidth(50);
        serverText.setMaxWidth(50);
        clientIP.setPrefWidth(100);
        clientPort.setPrefWidth(70);
        serverPort.setPrefWidth(70);
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(400);
        leftPanel.getChildren().addAll(
                gridPane,
                wrongData,
                dateStrt
        );
        Label labelUnak = new Label("Uнак = 0");
        Label labelUa = new Label("Ua = 0");
        Label labelFreq = new Label("Freq = 0");
        Label labelFluc = new Label("Fluc = 0");
        Label labelTimp = new Label("Тимп = 0");
        Label labelInak = new Label("Інак = 0");
        Label labelIa = new Label("Іа = 0");
        Label labelFlucMax = new Label("Fluc.max = 0");
        Label labelTimpN = new Label("tимп = 0");
        image1 = new ImageView();
        image1.setFitWidth(501);
        image1.setFitHeight(303);
        image1.setPreserveRatio(true);
        Canvas canvas = new Canvas(501, 303);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane imagePane = new StackPane();
        imagePane.getChildren().addAll(image1, canvas);
        Slider slider = new Slider(0, 501, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(Color.MAGENTA);
            gc.setLineWidth(2);
            gc.strokeLine(newVal.doubleValue(), 0, newVal.doubleValue(), canvas.getHeight());
        });
        VBox labelsBox = new VBox(10);
        labelsBox.getChildren().addAll(labelUnak, labelUa, labelFreq, labelFluc, labelTimp, labelInak, labelIa, labelFlucMax, labelTimpN);
        VBox imageAndSliderBox = new VBox(10);
        imageAndSliderBox.getChildren().addAll(imagePane, slider);
        HBox rightPanel = new HBox(10);
        rightPanel.getChildren().addAll(imageAndSliderBox, labelsBox);
        HBox centerPanel = new HBox(10);
        centerPanel.setPadding(new Insets(10));
        centerPanel.getChildren().addAll(leftPanel, rightPanel);
        staticText4 = new Label("Создание алгоритма");
        nameOfALG = new TextField();
        Button addButton = new Button("Добавить шаг");
        Button deleteButton = new Button("Удалить шаг");
        Button editButton = new Button("Изменить шаг");
        Button executeButton = new Button("Выполнить");
        CheckBox autoScrollCheckBox = new CheckBox("AUTOSCROLL");
        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.getChildren().addAll(staticText4, nameOfALG, addButton, deleteButton, editButton, executeButton, autoScrollCheckBox);
        numberofstep = new ComboBox<>();
        numberofstep.getItems().addAll("1", "2", "3");
        numberofstep.setValue("1");
        ComboBox<String> actionsComboBox = new ComboBox<>();
        actionsComboBox.getItems().addAll("BAD", "SET", "NUL");
        actionsComboBox.setValue("SET");
        ComboBox<String> unitComboBox = new ComboBox<>();
        unitComboBox.getItems().addAll("U1", "U2");
        unitComboBox.setValue("U1");
        value = new TextField("0100");
        change1 = new ComboBox<>();
        change1.getItems().addAll("(U1>0000)||(TS>0000)&&(FL<0000)G0001", "(U1>0000)||(TS>0000)&&(FL<0000)G0002");
        change1.setValue("(U1>0000)||(TS>0000)&&(FL<0000)G0001");
        change2 = new ComboBox<>();
        change2.getItems().addAll("(U1>0000)||(TS>0000)&&(FL<0000)G0001", "(U1>0000)||(TS>0000)&&(FL<0000)G0002");
        change2.setValue("(U1>0000)||(TS>0000)&&(FL<0000)G0001");
        change3 = new ComboBox<>();
        change3.getItems().addAll("(U1>0000)||(TS>0000)&&(FL<0000)G0001", "(U1>0000)||(TS>0000)&&(FL<0000)G0002");
        change3.setValue("(U1>0000)||(TS>0000)&&(FL<0000)G0001");
        GridPane algorithmGrid = new GridPane();
        algorithmGrid.setHgap(10);
        algorithmGrid.setVgap(10);
        algorithmGrid.setPadding(new Insets(10));
        algorithmGrid.add(new Label("Номер шага"), 0, 0);
        algorithmGrid.add(numberofstep, 0, 1);
        algorithmGrid.add(new Label("Действие"), 1, 0);
        algorithmGrid.add(actionsComboBox, 1, 1);
        algorithmGrid.add(new Label("Единица"), 2, 0);
        algorithmGrid.add(unitComboBox, 2, 1);
        algorithmGrid.add(value, 3, 1);
        algorithmGrid.add(new Label("Условие 1"), 4, 0);
        algorithmGrid.add(change1, 4, 1);
        algorithmGrid.add(new Label("Условие 2"), 5, 0);
        algorithmGrid.add(change2, 5, 1);
        algorithmGrid.add(new Label("Условие 3"), 6, 0);
        algorithmGrid.add(change3, 6, 1);
        stringgrid1 = new TableView<>();
        stringgrid1.setPlaceholder(new Label());
        stringgrid1.setPrefHeight(200);
        TableColumn<StepData, String> stepNumberColumn = new TableColumn<>("Номер шага");
        stepNumberColumn.setCellValueFactory(new PropertyValueFactory<>("stepNumber"));
        TableColumn<StepData, String> actionColumn = new TableColumn<>("Действие");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        TableColumn<StepData, String> condition1Column = new TableColumn<>("Условие перехода 1");
        condition1Column.setCellValueFactory(new PropertyValueFactory<>("condition1"));
        TableColumn<StepData, String> condition2Column = new TableColumn<>("Условие перехода 2");
        condition2Column.setCellValueFactory(new PropertyValueFactory<>("condition2"));
        TableColumn<StepData, String> condition3Column = new TableColumn<>("Условие перехода 3");
        condition3Column.setCellValueFactory(new PropertyValueFactory<>("condition3"));
        stringgrid1.getColumns().add(stepNumberColumn);
        stringgrid1.getColumns().add(actionColumn);
        stringgrid1.getColumns().add(condition1Column);
        stringgrid1.getColumns().add(condition2Column);
        stringgrid1.getColumns().add(condition3Column);
        ObservableList<StepData> data = FXCollections.observableArrayList(
                new StepData("1", "NUL", "(U1>0000)||(TS>0000)&&(FL<0000)G0001", "(U1>0000)||(TS>0000)&&(FL<0000)G0002", "(U1>0000)||(TS>0000)&&(FL<0000)G0002")
        );
        stringgrid1.setItems(data);
        VBox bottomPanel = new VBox(10);
        bottomPanel.getChildren().addAll(algorithmGrid, stringgrid1);
        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(topPanel, bottomPanel);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(centerPanel);
        borderPane.setBottom(mainLayout);
        Scene scene = new Scene(borderPane, 1255, 780, Color.BEIGE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Управление источником питания 1");
        primaryStage.show();
        connectServer.setOnAction(e -> connectServerClick());
        disconnectServer.setOnAction(e -> disconnectServer());
        clientConnect.setOnAction(e -> clientConnectClick());
        clientDisconnect.setOnAction(e -> disconnectClient());
        connectModular.setOnAction(e -> modulatorClick());
        save.setOnAction(e -> saveClick());
        read.setOnAction(e -> readClick());
        saveConfiguration.setOnAction(e -> saveConfigurationClick());
        readConfiguration.setOnAction(e -> readConfigurationClick());
        createNew.setOnAction(e -> createNewAlgorithm());
        saveAlg.setOnAction(e -> saveAlgorithm());
        readAlg.setOnAction(e -> read2Click("default_algorithm.txt"));
        edit.setOnAction(e -> edit2Click());
        auto.setOnAction(e -> auto1Click());
        manual.setOnAction(e -> {
            try {
                manual1Click();
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        startStop.setOnAction(e -> startStop1Click());
        stateInfo.setOnAction(e -> {
            if (idtcpClient1 != null && idtcpClient1.isConnected()) {
                new Thread(() -> {
                    try {
                        PrintWriter out = new PrintWriter(idtcpClient1.getOutputStream(), true);
                        out.println("IPX");
                        BufferedReader in = new BufferedReader(new InputStreamReader(idtcpClient1.getInputStream()));
                        String answer = in.readLine();
                        Platform.runLater(() -> getAnswer(answer));
                    } catch (IOException ex) {
                        Platform.runLater(() -> showMessage("Ошибка соединения с сервером"));
                        ex.printStackTrace();
                    }
                }).start();
            } else {
                showMessage("Соединение не установлено!");
            }
        });
    }

    private void offTimers() {
        autoTraining = false;
    }

    private void blackout(Socket client) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        for (int i = 0; i <= 9; i++) {
            out.println("IPD" + i);
            Thread.sleep(100);
        }
    }

    private void zeroAll(Socket client) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.println("IPS00000");Thread.sleep(150);
        out.println("IPS10000");Thread.sleep(150);
        out.println("IPS23980");Thread.sleep(150);
        out.println("IPS30000");Thread.sleep(150);
        out.println("IPS40000");Thread.sleep(150);
        out.println("IPS50000");Thread.sleep(150);
        out.println("IPS60000");Thread.sleep(150);
        out.println("IPS70000");Thread.sleep(150);
        out.println("IPS80000");Thread.sleep(150);
        out.println("IPS90000");Thread.sleep(150);
    }

    private String addZeros(int value) {
        String s = String.valueOf(value);
        while (s.length() < 4) s = "0" + s;
        return s;
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText(msg);
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    private boolean evaluateSingleCondition(String cond) {
        if (cond.length()<8) return false;
        String param = cond.substring(1,3);
        char operator = cond.charAt(3);
        double val = Double.parseDouble(cond.substring(4,8))/10.0;
        double currentValue=0;
        if (param.startsWith("U")) {
            int uIndex = Integer.parseInt(param.substring(1));
            currentValue = log1[logPart].data[uIndex];
        } else if (param.startsWith("I")) {
            int iIndex = Integer.parseInt(param.substring(1))+10;
            currentValue = log1[logPart].data[iIndex];
        } else if (param.equals("TS")) {
            currentValue = stepTimeCounter;
        } else if (param.equals("FL")) {
            currentValue = log1[logPart].data[3];
        } else if (param.equals("FR")) {
            currentValue = log1[logPart].data[2];
        } else if (param.equals("WI")) {
            currentValue = log1[logPart].data[13];
        }
        switch (operator) {
            case '>': return currentValue>val;
            case '<': return currentValue<val;
            case '=': return currentValue==val;
        }
        return false;
    }

    private boolean changeCheck(String change) {
        if (change.length()<30) return false;
        String cond1 = change.substring(0,10);
        String cond2 = change.substring(10,20);
        String cond3 = change.substring(20,30);
        char op1='|';
        char op2='&';
        boolean c1 = evaluateSingleCondition(cond1);
        boolean c2 = evaluateSingleCondition(cond2);
        boolean c3 = evaluateSingleCondition(cond3);
        boolean c12 = (op1=='&')?(c1&&c2):(c1||c2);
        boolean result=(op2=='&')?(c12&&c3):(c12||c3);
        return result;
    }

    private int parseStepFromCondition(String c) {
        int idx=c.indexOf("G000");
        if (idx>=0&&c.length()>=idx+7) {
            String stepStr=c.substring(idx+4,idx+7);
            return Integer.parseInt(stepStr);
        }
        return actualStep;
    }

    private int extractNextStep(String ch1,String ch2,String ch3) {
        if (changeCheck(ch1)) return parseStepFromCondition(ch1);
        if (changeCheck(ch2)) return parseStepFromCondition(ch2);
        if (changeCheck(ch3)) return parseStepFromCondition(ch3);
        return actualStep;
    }

    private int algorithmImpact(int step) {
        JSONObject stepObj=stepsArray.getJSONObject(step);
        String act=stepObj.getString("action");
        String ch1=stepObj.getString("change1");
        String ch2=stepObj.getString("change2");
        String ch3=stepObj.getString("change3");
        String command="IPX";
        int nextStep=stepObj.getInt("number")-1;

        if (act.startsWith("SET")) {
            String paramType=act.substring(3,4);
            int index=Integer.parseInt(act.substring(4,5));
            int value=Integer.parseInt(act.substring(5,9));
            if (paramType.equals("U")) {
                value=(int)Math.round(value/coefficients[index]);
                if (index==0) value=value/10;
                command="IPS"+index+addZeros(value);
            } else if (paramType.equals("I")) {
                value=(int)Math.round(value/(coefficients[index+10]*10));
                command="IPS"+index+addZeros(value);
            }
        } else if (act.startsWith("OFF")) {
            String valPart=act.substring(3,8);
            command="IPD"+valPart;
        } else if (act.startsWith("NUL")) {
            command="IPX";
        } else if (act.startsWith("BAD")) {
            command="offall";
            offTimers();
        } else if (act.startsWith("END")) {
            command="end";
            offTimers();
        } else if (act.startsWith("DAL")) {
            command="prepareall";
        } else if (act.startsWith("ENL")) {
            double currentU0=log1[logPart].data[0]/coefficients[0];
            int stepOfIncandescence=13;
            int maxIncandescence=118;
            int val=(int)currentU0;
            if (val+stepOfIncandescence<maxIncandescence && stepTimeCounter<(Double.parseDouble(edit1.getText())*1.5)) {
                val=val+stepOfIncandescence;
            }
            command="IPS0"+addZeros(val);
        }

        if (!act.startsWith("BAD")&&!act.startsWith("END")) {
            boolean condMet=changeCheck(ch1)||changeCheck(ch2)||changeCheck(ch3);
            if (condMet) {
                nextStep=extractNextStep(ch1,ch2,ch3);
                startOfStep=System.currentTimeMillis();
            } else {
                nextStep=stepObj.getInt("number")-1;
            }
        }
        commandSender(command);
        return nextStep;
    }

    private void commandSender(String command) {
        try {
            if (command.equals("offall")) {
                if (powerClient!=null&&powerClient.isConnected()) {
                    blackout(powerClient);
                    Thread.sleep(200);
                    zeroAll(powerClient);
                }
            } else if (command.equals("prepareall")) {
                if (powerClient!=null&&powerClient.isConnected()) {
                    blackout(powerClient);
                    Thread.sleep(200);
                    zeroAll(powerClient);
                }
            } else if (command.equals("end")) {
                if (powerClient!=null&&powerClient.isConnected()) {
                    blackout(powerClient);
                    Thread.sleep(200);
                    zeroAll(powerClient);
                }
                offTimers();
            } else {
                if (powerClient!=null&&powerClient.isConnected()) {
                    PrintWriter out=new PrintWriter(powerClient.getOutputStream(),true);
                    out.println(command);
                }
            }
        } catch (IOException|InterruptedException e) {
            showMessage("Ошибка отправки команды: "+e.getMessage());
        }
    }

    private void restartAlgorithmClick() {
        actualStep=0;
        refreshButtonClick();
    }

    private void refreshButtonClick() {
        arraysRefresh();
        logPart=0;
    }

    private void arraysRefresh() {
        for (int i=0;i<=86400;i++){
            if (log1[i]==null) log1[i]=new LogLine1();
            log1[i].time=0;
            for (int j=0;j<20;j++){
                log1[i].data[j]=0;
            }
        }
    }

    public void startOfLogClick() {
        if (idtcpClient1!=null&&idtcpClient1.isConnected()){
            if (logStart.getText().equals("Log start")&&(!isTimer1Running||!isTimer2Running)){
                if (!Pattern.compile("[0-9]").matcher(measureName.getText()).find()){
                    showMessage("Введите номер прибора в формате: \"123_abc\"");
                } else {
                    logStart.setText("Log stop");
                    timer2.schedule(timerTask2,0,1000);
                    isTimer2Running=true;
                    File logFile = new File("logfile.txt");
                    if (logFile.exists()) {
                        history.appendText("Файл логов успешно прочитан!\n");
                    }
                }
            } else if (logStart.getText().equals("Log stop")&&(isTimer1Running||isTimer2Running)){
                logStart.setText("Log start");
                timerTask1.cancel();
                timerTask2.cancel();
                isTimer1Running=false;
                isTimer2Running=false;
            }
        } else {
            showMessage("Отсутствует подключение!");
        }
        if (!isTimer3Running){
            timer3.schedule(timerTask3,0,50);
            isTimer3Running=true;
        }
    }

    public void startStop1Click() {
        try {
            if (idtcpClient1!=null) idtcpClient1.getOutputStream().flush();
            if (powerClient!=null) powerClient.getOutputStream().flush();
            if (autoTraining && logStart.getText().equals("Log start")) {
                startOfLogClick();
            } else {
                startOfLogClick();
                autoTraining=!autoTraining;
            }
        } catch (IOException e) {
            showMessage("Ошибка при очистке буфера сокетов.");
            e.printStackTrace();
        }
    }

    public void manual1Click() throws IOException,InterruptedException {
        if (powerClient.isConnected()){
            nameOfALG.setText(jsonAlgorithm.get("name").toString().replace("\"",""));
            powerClient.getOutputStream().write("IPS00000\n".getBytes());
            Thread.sleep(50);
            powerClient.getOutputStream().write("IPS10000\n".getBytes());
        } else {
            showMessage("Отсутствует подключение!");
        }
    }

    public void auto1Click() {
    }

    public void edit2Click() {
    }

    public void read2Click(String defaultName) {
        String nameOfFile;
        String jsonString="";
        FileChooser fileChooser=new FileChooser();
        if (defaultName==null||defaultName.isEmpty()) {
            File selectedFile=fileChooser.showOpenDialog(new Stage());
            if (selectedFile!=null) {
                nameOfFile=selectedFile.getAbsolutePath();
            } else {
                return;
            }
        } else {
            nameOfFile=defaultName;
        }
        try {
            jsonString=new String(Files.readAllBytes(Paths.get(nameOfFile)));
        } catch (NoSuchFileException e) {
            showMessage("Файл не найден: "+nameOfFile);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonAlgorithm=new JSONObject(jsonString);
        stepsArray=jsonAlgorithm.getJSONArray("steps");
        double k_y_19=image1.getFitHeight()/stepsArray.length();
        numberofstep.getItems().clear();
        stringgrid1.getItems().clear();
        nameOfALG.setText(jsonAlgorithm.getString("name"));
        for (int i=0;i<stepsArray.length();i++){
            JSONObject step=stepsArray.getJSONObject(i);
            numberofstep.getItems().add(Integer.toString(i+1));
            stringgrid1.getItems().add(new StepData(
                    String.valueOf(step.getInt("number")),
                    step.getString("action"),
                    step.getString("change1"),
                    step.getString("change2"),
                    step.getString("change3")
            ));
        }
        k_y[19]=k_y_19;
    }

    public void saveAlgorithm() {
        String jsonFileName=nameOfALG.getText()+"_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".txt";
        try(FileWriter fileWriter=new FileWriter(jsonFileName)){
            fileWriter.write(jsonAlgorithm.toString());
            fileWriter.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void createNewAlgorithm() {
        stepsArray=new JSONArray();
        staticText4.setVisible(true);
        numberofstep.setVisible(true);
        value.setVisible(true);
        change1.setVisible(true);
        change2.setVisible(true);
        change3.setVisible(true);
        numberofstep.getItems().clear();
        change1.getItems().clear();
        change1.getItems().add("(U1>0000)||(TS>0000)&&(FL<0000)GO001");
        change1.setValue("(U1>0000)||(TS>0000)&&(FL<0000)GO001");
        change2.getItems().clear();
        change2.getItems().add("(U1>0000)||(TS>0000)&&(FL<0000)GO001");
        change2.setValue("(U1>0000)||(TS>0000)&&(FL<0000)GO001");
        change3.getItems().clear();
        change3.getItems().add("(U1>0000)||(TS>0000)&&(FL<0000)GO001");
        change3.setValue("(U1>0000)||(TS>0000)&&(FL<0000)GO001");
    }

    private void saveClick() {
        String measureNameText=measureName.getText();
        if (!Pattern.compile("[0-9]").matcher(measureNameText).find()){
            showMessage("Пожалуйста, введите корректное имя измерения");
        } else {
            saveFunctionClick();
        }
    }

    private void saveFunctionClick() {
        String name;
        if (measureName.getText().isEmpty()) {
            showMessage("Имя измерения не может быть пустым!\nАвтоматически генерируемое имя.");
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            name="measure_"+dateFormat.format(new Date());
        } else {
            name=measureName.getText()+"_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        File file=fileChooser.showSaveDialog(null);
        if (file!=null) {
            try {
                Files.write(Paths.get(file.getAbsolutePath()),name.getBytes());
                history.appendText("Данные сохранены в "+file.getAbsolutePath()+"\n");
            } catch(IOException e){
                showMessage("Ошибка при сохранении файла");
                e.printStackTrace();
            }
        }
    }

    private void readClick() {
        readFunctionClick();
    }

    private void readFunctionClick() {
        File file=fileChooser.showOpenDialog(null);
        if (file!=null) {
            try {
                byte[] fileContent=Files.readAllBytes(file.toPath());
                String content=new String(fileContent);
                history.appendText("Данные прочитаны из "+file.getAbsolutePath()+"\n");
                String nameOfFile=file.getName();
                if (nameOfFile.endsWith(".txt")) {
                    nameOfFile=nameOfFile.substring(0,nameOfFile.length()-4);
                }
                measureName.setText(nameOfFile);
            } catch(IOException e){
                showMessage("Ошибка при чтении файла");
                e.printStackTrace();
            }
        }
    }

    private void connectServerClick() {
        try {
            int port=Integer.parseInt(clientPort.getText());
            serverSocket=new ServerSocket(port);
            history.appendText("Server connected on port "+port+"\n");
            connectServer.setDisable(true);
            disconnectServer.setDisable(false);
            if (adminItem.isSelected()) {
                clientConnect.setVisible(true);
            } else {
                clientConnect.setVisible(false);
            }
            new Thread(() -> {
                try {
                    while(!serverSocket.isClosed()){
                        Socket clientSocket=serverSocket.accept();
                        history.appendText("Client connected: "+clientSocket.getInetAddress()+"\n");
                    }
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch(IOException e) {
            showMessage("Listen Error");
            e.printStackTrace();
        } catch(NumberFormatException e){
            showMessage("Invalid Port Number");
            e.printStackTrace();
        }
    }

    private void clientConnectClick() {
        try {
            int port=Integer.parseInt(clientPort.getText());
            String host=clientIP.getText();
            idtcpClient1=new Socket();
            idtcpClient1.connect(new InetSocketAddress(host,port),3000);
            powerClient=new Socket(host,45644);
            history.appendText("Client connected to server at "+host+":"+port+"\n");
            clientConnect.setDisable(true);
            clientDisconnect.setDisable(false);
        } catch(IOException e){
            showMessage("Client Connection Error");
            e.printStackTrace();
        }
    }

    private void disconnectServer() {
        try {
            if (serverSocket!=null&&!serverSocket.isClosed()) {
                serverSocket.close();
                history.appendText("Server disconnected\n");
                connectServer.setDisable(false);
                disconnectServer.setDisable(true);
                clientConnect.setVisible(false);
            }
        } catch(IOException e){
            showMessage("Error during disconnection");
            e.printStackTrace();
        }
    }

    private void disconnectClient() {
        try {
            if (idtcpClient1!=null&&!idtcpClient1.isClosed()){
                idtcpClient1.close();
                history.appendText("Client disconnected\n");
                clientConnect.setDisable(false);
                clientDisconnect.setDisable(true);
            }
        } catch(IOException e){
            showMessage("Error during client disconnection");
            e.printStackTrace();
        }
    }

    private void modulatorClick() {
        try {
            if (powerClient!=null&&powerClient.isConnected()&&idtcpClient1!=null&&idtcpClient1.isConnected() &&
                    powerClient.getInetAddress()!=null&&idtcpClient1.getInetAddress()!=null &&
                    powerClient.getInetAddress().equals(idtcpClient1.getInetAddress())) {
                powerClient.close();
                powerClient=new Socket();
                powerClient.connect(new InetSocketAddress(clientIP.getText(),45644),3000);
                clientConnect.setDisable(false);
                clientDisconnect.setDisable(true);
                if (timer3!=null) timer3.cancel();
            } else if (idtcpClient1!=null&&idtcpClient1.isConnected() &&
                    powerClient.getInetAddress()!=null&&idtcpClient1.getInetAddress()!=null &&
                    powerClient.getInetAddress().equals(idtcpClient1.getInetAddress())) {
                powerClient=idtcpClient1;
                if (!idtcpClient1.isConnected()) clientConnectClick();
                history.appendText("Clients merged!\n");
            } else {
                powerClient=new Socket();
                powerClient.connect(new InetSocketAddress(clientIP.getText(),45644),3000);
                if (powerClient.isConnected()) {
                    history.appendText("Modulator: Connected\n");
                }
            }
            if (powerClient.isConnected()) {
                history.appendText("Modulator: Connected\n");
            }
        } catch(IOException e){
            showMessage("Ошибка при подключении модулятора");
        }
    }

    private void saveConfigurationClick() {
        config.clientIP=clientIP.getText();
        config.clientPort=clientPort.getText();
        config.serverPort=serverPort.getText();
        config.scale_Y=k_y;
        config.coefficients=coefficients;
        config.showCurve[0]=u1.isSelected();
        config.showCurve[1]=u2.isSelected();
        config.showCurve[2]=u3.isSelected();
        config.showCurve[3]=u4.isSelected();
        config.showCurve[4]=u5.isSelected();
        config.showCurve[5]=u6.isSelected();
        config.showCurve[6]=u7.isSelected();
        config.showCurve[7]=u8.isSelected();
        config.showCurve[8]=u9.isSelected();
        config.showCurve[9]=u10.isSelected();
        config.showCurve[10]=i1.isSelected();
        config.showCurve[11]=i2.isSelected();
        config.showCurve[12]=i3.isSelected();
        config.showCurve[13]=i4.isSelected();
        config.showCurve[14]=i5.isSelected();
        config.showCurve[15]=i6.isSelected();
        config.showCurve[16]=i7.isSelected();
        config.showCurve[17]=i8.isSelected();
        config.showCurve[18]=i9.isSelected();
        config.showCurve[19]=i10.isSelected();
        TextInputDialog dialog=new TextInputDialog("CONF_");
        dialog.setTitle("Save Configuration");
        dialog.setHeaderText("Введите название конфигурации:");
        Optional<String> result=dialog.showAndWait();
        if (result.isPresent()){
            String fileName=result.get()+".byt";
            try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(fileName))){
                oos.writeObject(config);
                history.appendText("Configuration saved to "+fileName+"\n");
            } catch(IOException e){
                showMessage("Error saving configuration");
                e.printStackTrace();
            }
        }
    }

    private void readConfigurationClick() {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Open Configuration File");
        File file=fileChooser.showOpenDialog(null);
        if (file!=null){
            try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file))){
                config=(Configuration)ois.readObject();
                history.appendText("Configuration loaded from "+file.getName()+"\n");
                clientIP.setText(config.clientIP);
                clientPort.setText(config.clientPort);
                serverPort.setText(config.serverPort);
                k_y=config.scale_Y;
                coefficients=config.coefficients;
                u1.setSelected(config.showCurve[0]);
                u2.setSelected(config.showCurve[1]);
                u3.setSelected(config.showCurve[2]);
                u4.setSelected(config.showCurve[3]);
                u5.setSelected(config.showCurve[4]);
                u6.setSelected(config.showCurve[5]);
                u7.setSelected(config.showCurve[6]);
                u8.setSelected(config.showCurve[7]);
                u9.setSelected(config.showCurve[8]);
                u10.setSelected(config.showCurve[9]);
                i1.setSelected(config.showCurve[10]);
                i2.setSelected(config.showCurve[11]);
                i3.setSelected(config.showCurve[12]);
                i4.setSelected(config.showCurve[13]);
                i5.setSelected(config.showCurve[14]);
                i6.setSelected(config.showCurve[15]);
                i7.setSelected(config.showCurve[16]);
                i8.setSelected(config.showCurve[17]);
                i9.setSelected(config.showCurve[18]);
                i10.setSelected(config.showCurve[19]);
            } catch(IOException|ClassNotFoundException e){
                showMessage("Error loading configuration");
                e.printStackTrace();
            }
        }
    }

    private void getAnswer(String answer) {
        history.appendText("Server Response: "+answer+"\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
