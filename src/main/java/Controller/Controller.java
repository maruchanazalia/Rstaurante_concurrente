package Controller;

import Fuctions.Chef;
import Fuctions.CreateClient;
import Fuctions.Waiter;
import Fuctions.Receptionist;
import Model.Restaurant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;


import java.util.Observable;
import java.util.Observer;


public class Controller implements Observer {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button btnInit;

    @FXML
    private Button btnSalir;

    @FXML
    private ProgressBar progressBarTotal;

    @FXML
    private Label txtPercentage;

    @FXML
    private Label lblwaiter1;


    @FXML
    void Finalizar(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void IniciarAnimacion(ActionEvent event) {
        btnInit.setDisable(true);
        txtPercentage.setText("0");
        progressBarTotal.setProgress(0);
        System.out.println("Inicio de animacion");
        Restaurant restaurant = new Restaurant();
        restaurant.addObserver(this);

        Waiter waiter =new Waiter(anchor, restaurant);
        Thread hiloWaiter = new Thread(waiter);
        hiloWaiter.setDaemon(true);
        hiloWaiter.start();


        Receptionist receptionist =new Receptionist(restaurant);
        Chef chef = new Chef(restaurant);
        CreateClient createClient = new CreateClient(anchor, restaurant, this);

        Thread hiloReceptionist = new Thread(receptionist);
        Thread hiloChef = new Thread(chef);
        Thread hiloCreateClient = new Thread(createClient);

        hiloReceptionist.setDaemon(true);
        hiloReceptionist.start();
        hiloChef.setDaemon(true);
        hiloChef.start();
        hiloCreateClient.setDaemon(true);
        hiloCreateClient.start();

    }

    Alert alert = new Alert((Alert.AlertType.CONFIRMATION));
    @Override
    public void update(Observable o, Object arg) {
        synchronized (this) {
            if (((String)arg).contains("ocupadoMesero")){
                String[] cadena = ((String) arg).split(" ");
                int numMesa = Integer.parseInt(cadena[1]);
                Platform.runLater(()-> lblwaiter1.setText("Atendiendo: "+(numMesa+1)));
            }else
            if (((String)arg).contains("libreMesero")){
                Platform.runLater(()-> lblwaiter1.setText(""));
            }else
            if (((String)arg).contains("ocupado")){
            }else if(((String)arg).contains("libre")){
            }else
            if(((String)arg).contains("seat")) {
                String[] cadena = ((String) arg).split(" ");
                int numMesa = Integer.parseInt(cadena[1]);
            }
            else {
                int dato= Integer.parseInt((String)arg);
                System.out.println("Observer "+dato);
                double valor=dato*(0.01);
                Platform.runLater(()->  progressBarTotal.setProgress(valor));
                int porciento= (int) (valor*100);
                Platform.runLater(()-> txtPercentage.setText(porciento+" %"));
                if(porciento==100){
                    alert.setHeaderText("Se atendieron todos los clientes");
                    alert.setTitle("RESTAURANTE CERRADO");
                    alert.setContentText("Restaurant Concurrente");
                    Platform.runLater(()-> {
                        alert.show();
                        btnInit.setDisable(false);
                    });
                }
            }
        }
    }
}
