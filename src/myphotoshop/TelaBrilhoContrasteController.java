
package myphotoshop;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import myphotoshop.transformacoes.OpenCV;

public class TelaBrilhoContrasteController implements Initializable 
{
    @FXML
    private Slider brilho;
    @FXML
    private ImageView imgView;
    @FXML
    private Slider contraste;
    private Image nimg;
    @FXML
    private Label lbBrilho;
    @FXML
    private Label lbContraste;
    @Override    
    public void initialize(URL url, ResourceBundle rb) 
    {
        TelaPrincipalController.brilho = 0;
        TelaPrincipalController.contraste = 0;
        contraste.setValue(TelaPrincipalController.contraste);
        brilho.setValue(TelaPrincipalController.brilho);
        
        nimg = OpenCV.Brilho(TelaPrincipalController.img, (contraste.getValue() * 2 / 100) + 1 , brilho.getValue());
        imgView.setImage(nimg);
        imgView.setFitWidth(nimg.getWidth());
        imgView.setFitHeight(nimg.getHeight());        
        imgView.setX(imgView.getImage().getWidth() / 2);
        imgView.setLayoutY(imgView.getImage().getHeight()/ 2);
    }

    @FXML
    private void evtAttValor(MouseEvent event)
    {
        nimg = OpenCV.Brilho(TelaPrincipalController.img, (contraste.getValue() * 2 / 100) + 1 , brilho.getValue());
        imgView.setImage(nimg);
        imgView.setFitWidth(nimg.getWidth());
        imgView.setFitHeight(nimg.getHeight());
        lbBrilho.setText(String.valueOf((int)brilho.getValue()));
    }

    @FXML
    private void evtAplicar(ActionEvent event)
    {
        TelaPrincipalController.alterar = true;       
        TelaPrincipalController.brilho = brilho.getValue();
        TelaPrincipalController.contraste = (contraste.getValue() * 2 / 100) + 1;
        
        Stage stage = (Stage) lbBrilho.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void evtCancelar(ActionEvent event)
    {
        Stage stage = (Stage) lbBrilho.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void evtAttValor1(MouseEvent event) 
    {
        nimg = OpenCV.Brilho(TelaPrincipalController.img, (contraste.getValue() * 3 / 100) + 1 , brilho.getValue());
        imgView.setImage(nimg);
        imgView.setFitWidth(nimg.getWidth());
        imgView.setFitHeight(nimg.getHeight());
        lbContraste.setText(String.valueOf((int)contraste.getValue()));
    }
}
