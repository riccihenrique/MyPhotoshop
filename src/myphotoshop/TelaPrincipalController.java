package myphotoshop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JOptionPane;
import myphotoshop.transformacoes.Basicas;
import myphotoshop.transformacoes.OpenCV;

public class TelaPrincipalController implements Initializable 
{
    public static int valor_desfoque = 0;
    public static Image img;
    public static File arq = null;
    public static boolean alterar = false;
    public static double brilho = 0.0, contraste = 0.0, rotacao = 0.0, escala = 0.0;
    
    private Stack<Image> pilha_volta = new Stack<Image>();
    private Stack<Image> pilha_vai = new Stack<Image>();
    private boolean caneta = false;
    private boolean edit = false;
    private FileChooser fc = new FileChooser();
    
    @FXML
    private ImageView imageview;
    @FXML
    private MenuItem bts;
    @FXML
    private MenuItem btsc;
    @FXML
    private Menu btf;
    @FXML
    private ColorPicker color;
    @FXML
    private Button btdesenhar;
    @FXML
    private Button btsv;
    @FXML
    private Button btsvc; 
    @FXML
    private Button btvoltar;
    @FXML
    private Button btir;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), 
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"));
        habi_desab(false);
        btvoltar.setDisable(true);
        btir.setDisable(true);
    }
    
    private void habi_desab(boolean t)
    {
        btsc.setDisable(!t);
        btf.setDisable(!t);
        bts.setDisable(!t);
        color.setDisable(!t);
        btdesenhar.setDisable(!t);
        btsv.setDisable(!t);
        btsvc.setDisable(!t);
    }
    
    private void movPilha()
    {
        if(pilha_volta.isEmpty())
            btvoltar.setDisable(true);
        else
            btvoltar.setDisable(false);
        if(pilha_vai.isEmpty())
            btir.setDisable(true);
        else
            btir.setDisable(false);
    }
    
    private void efeito()
    {
        pilha_volta.push(imageview.getImage());
        movPilha();
    }
    
    @FXML
    private void evtAbrir(ActionEvent event) 
    {        
        arq = fc.showOpenDialog(null);
        if(arq != null)
        {
            Image img = new Image(arq.toURI().toString());
            imageview.setImage(img);
            imageview.setFitWidth(img.getWidth());
            imageview.setFitHeight(img.getHeight());
            habi_desab(true);
        }       
        ((Stage) (imageview.getScene().getWindow())).setTitle(arq.getAbsolutePath() + " - MyPhotoShop");
    }
    
    private void salvar() throws IOException            
    {
        BufferedImage bimg = SwingFXUtils.fromFXImage(imageview.getImage(), null);
        String caminho = arq.getAbsolutePath(), ext;
        ext = caminho.substring(caminho.lastIndexOf(".")+1);
        
        if(ext.equals("png") || ext.equals("gif"))
               ImageIO.write(bimg, ext, arq);
        else
        {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(ext);
            ImageWriter writer = (ImageWriter) writers.next();
            // Cria um conjunto de parâmetros para configuração
            ImageWriteParam param = writer.getDefaultWriteParam();
            // Altera para não usar compressão automática
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            // Muda a taxa de compressão para 100% (valor entre 0 e 1)
            param.setCompressionQuality(1);
            // Salva a imagem
            FileImageOutputStream output = new FileImageOutputStream(arq);
            writer.setOutput(output);
            writer.write(null, new IIOImage(bimg, null, null), param);
        }
        pilha_vai.clear();
        pilha_volta.clear();
        btir.setDisable(true);
        btvoltar.setDisable(true);
        edit = false;
    }

    @FXML
    private void evtSalvar(ActionEvent event) throws IOException 
    {
        salvar();
    }

    @FXML
    private void evtSalvarComo(ActionEvent event) throws IOException 
    {
        arq = fc.showSaveDialog(null);
        if(arq != null)
        {
            salvar();
            ((Stage) (imageview.getScene().getWindow())).setTitle(arq.getAbsolutePath() + " - MyPhotoShop");
        }        
    }

    @FXML
    private void evtFechar(ActionEvent event) throws IOException
    {
        if(edit)
            if(JOptionPane.showConfirmDialog(null, "Deseja salvar alterações feitas?", "Aviso", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                evtSalvar(event);           
        Platform.exit();
    }

    @FXML
    private void evtTonsCinza(ActionEvent event)
    {
        edit = true;
        efeito();
        imageview.setImage(Basicas.tonsCinza(imageview.getImage()));
    }

    @FXML
    private void evtSobre(ActionEvent event) throws IOException
    {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("TelaSobre.fxml"));  
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sobre");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void evtCaneta(ActionEvent event)
    {
        caneta = !caneta;
    }
    
    @FXML
    private void evtSegurar(MouseEvent event)
    {
        if(caneta)
            efeito();
    }

    @FXML
    private void evtDesenha(MouseEvent event) 
    { 
        if(caneta)
        {
            edit = true;
            imageview.setImage(Basicas.caneta(imageview.getImage(), (int)event.getX(), (int)event.getY(), color.getValue()));
        }
    }

    @FXML
    private void evtEspelhaHori(ActionEvent event) 
    {
        efeito();
        edit = true;
        imageview.setImage(Basicas.EspelhaHorizontal(imageview.getImage()));
    }

    @FXML
    private void evtEspelhaVerti(ActionEvent event) 
    {
        efeito();
        edit = true;
        imageview.setImage(Basicas.EspelhaVertical(imageview.getImage()));
    }

    @FXML
    private void evtGaussiano(ActionEvent event) throws IOException
    {
        img = imageview.getImage();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("TelaDesfoqueGaussiano.fxml"));  
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Desfoque Gaussiano");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        if(alterar)
        {
            efeito();
            edit = true;
            imageview.setImage(OpenCV.suavizacao_gausiana(imageview.getImage(), valor_desfoque));
            alterar = false;
        }
    }

    @FXML
    private void evtVoltar(ActionEvent event)
    {
        pilha_vai.push(imageview.getImage());
        imageview.setImage(pilha_volta.pop());
        movPilha();
    }

    @FXML
    private void evtIr(ActionEvent event)
    {
        pilha_volta.push(imageview.getImage());
        imageview.setImage(pilha_vai.pop());
        movPilha();
    }    


    @FXML
    private void evtEscala(ActionEvent event) throws IOException 
    {        
        img = imageview.getImage();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("TelaEscalaRotacao.fxml"));  
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Ajuste de Imagem");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        if(alterar)
        {
            efeito();
            edit = true;
            imageview.setImage(OpenCV.escala(imageview.getImage(), rotacao, escala));
            alterar = false;
        }
    }

    @FXML
    private void btBrilho(ActionEvent event) throws IOException 
    {
        img = imageview.getImage();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("TelaBrilhoContraste.fxml"));  
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Ajuste de Imagem");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        if(alterar)
        {
            efeito();
            edit = true;
            imageview.setImage(OpenCV.Brilho(imageview.getImage(), contraste, brilho));
            alterar = false;
        }               
    }

    @FXML
    private void evtBorda(ActionEvent event)
    {
        efeito();
        edit = true;
        imageview.setImage(OpenCV.borda_prewitt(imageview.getImage()));
    }

    @FXML
    private void evtPanorama(ActionEvent event) 
    {
        efeito();
        FileChooser fc1 = new FileChooser(), fc2 = new FileChooser();        
        fc1.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), 
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"));
        fc2.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), 
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"));
        
        
        File arq1 = fc1.showOpenDialog(null), arq2 = fc2.showOpenDialog(null);
        
        imageview.setImage(OpenCV.panorama(imageview.getImage(), new Image(arq1.toURI().toString()), new Image(arq2.toURI().toString())));
    }
}
