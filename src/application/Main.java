package application;
	
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	static Stage mainWindow;
	static Button startButton = new Button("Start");
	static Button settingsButton = new Button("Settings");
	static Button verify = new Button("verify");
	static Button next = new Button("Next");
	static Button locking = new Button();
	static int totalRight=0;
	static int totalQuestions=0;
	static int totalSolved=0;
	static int numberSize = 2;
	static int questionSize = 10;
	static int numberOfsets = 2;
	static int numberOfProblemsperSet=10;
	static Label score = new Label("current score "+totalRight+"/"+totalQuestions+ "\n" +" total solved "+totalSolved); 
	static VBox currentProblems;
	static Scene scene1 ;
	static Stage mainStage = new Stage();
	@Override
	public void start(Stage primaryStage) {
		
		activateDynamics();
		GridPane gridPane = createStartPage();
//		Scene scene1 = new Scene(generatePage(10, 3, 10, 2));
		scene1 = new Scene(gridPane);
		scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		mainStage.setScene(scene1);
		mainStage.setTitle("Mental Arithmetic Practice");
		mainStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
//------------------------------------------dynamics-------------------------------------------------------------
  public void activateDynamics() {
	  startButton.setOnMouseClicked(new EventHandler() {
			@Override
			public void handle(Event e) {
					newSet();
			}
		});
	  verify.setOnMouseClicked(new EventHandler() {
			@Override
			public void handle(Event e) {
			  markAnswers();		
			}
		});
	  next.setOnMouseClicked(new EventHandler() {
			@Override
			public void handle(Event e) {
			  if(totalQuestions==totalRight) {
				  totalSolved+=totalQuestions;
				  totalRight=0;
				  score.setText("current score "+totalRight+"/"+totalQuestions+ "\n" +" total solved "+totalSolved); 
				  newSet();
			  }		
			}
		});
	  settingsButton.setOnMouseClicked(new EventHandler() {
			@Override
			public void handle(Event e) {
				Stage settingsStage = new Stage();		
			    Scene scene2 = new Scene(settingsPage());
				scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				settingsStage.setScene(scene2);
				settingsStage.setTitle("Settings");
				settingsStage.show();
			}
		});
  }
//------------------------------------------dynamics-------------------------------------------------------------
	
//------------------------------------------problems section---------------------------------------------------------------	
   public void newSet() {
	    scene1=new Scene(generatePage(questionSize, numberSize, numberOfProblemsperSet, numberOfsets));
		scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		mainStage.setScene(scene1);
		mainStage.setTitle("Mental Arithmetic Practice");
		mainStage.show();
   }
  
	public static long generateNumber(int len) {
		StringBuilder amount = new StringBuilder("1");
		while(len-->0) amount.append("0");		
		long number = (long)(Math.random()*Long.parseLong(amount.toString()));
		return number;
	}
	
	public static HBox generatePage(int rows, int NumLen , int numberOfProblems , int numberOfProblemSets) {
		HBox page = new HBox();
		page.getChildren().addAll(generateTotalProblems(rows, NumLen, numberOfProblems, numberOfProblemSets),generateControls());
		return page;
	}
	
	
	public static VBox generateProblem(int rows, int NumLen) {
		VBox problem = new VBox();
		long answer=0;
		int negTime=0;
		
		while(rows-->0) {
			Label problemElement = null; 
			long current = generateNumber(NumLen);
			if(negTime>=2&&answer>=current) {
				negTime=0;
				problemElement = new Label("-"+current); 
				answer-=current;
				
			}else {
				negTime++;
				answer+=current;
				problemElement = new Label("+"+current); 
			
			}
			problemElement.setAlignment(Pos.CENTER);
			problem.getChildren().add(problemElement);
			problem.prefWidthProperty().bind(problemElement.widthProperty());

		}
		
	    TextField answerField = new TextField();
		problem.getChildren().add(answerField);
		problem.getStyleClass().add("vbox");
		return problem;
	}
	
	public static HBox generateProblemSet(int rows, int NumLen , int numberOfProblems) {
		HBox problemSet = new HBox();
		
		while(numberOfProblems-->0) {
			problemSet.getChildren().add(new Group(generateProblem(rows, NumLen)));
		}

		return problemSet;
	}
	
	public static VBox generateTotalProblems(int rows, int NumLen , int numberOfProblems , int numberOfProblemSets) {
		VBox page = new VBox();
		while(numberOfProblemSets-->0) {
			page.getChildren().add(generateProblemSet( rows,  NumLen ,  numberOfProblems));
		}
		
		return currentProblems = page;
	}
	
	public static void markAnswers() {
		totalQuestions=0;
		totalRight=0;
		ObservableList<Node> rows = currentProblems.getChildren();
		for(Node node : rows) {
			ObservableList<Node> problems = ((HBox)node).getChildren();
		  for(Node ProblemGroup : problems) {
			  ObservableList<Node> vboxs = ((Group)ProblemGroup).getChildren();
			  for(Node problem : vboxs) {
				  ObservableList<Node> elements = ((VBox)problem).getChildren();
				  long answer = 0;
				  long yours = -1;
				  for(int i=0;i<elements.size();i++) {
					  if(i==elements.size()-1) {
						  if(!((TextField)elements.get(i)).getText().equals(""))
						    yours = Long.parseLong(((TextField)elements.get(i)).getText());
					  }else{
						  answer+= Long.parseLong(((Label)elements.get(i)).getText());
					  }
				  }
				  System.out.println("answer "+" "+answer);
				  System.out.println(answer==yours);
				  totalQuestions++;
				  if(answer==yours) {
					  totalRight++;
					  ((TextField)elements.get(elements.size()-1)).setId("correct");
				  }else {
					  ((TextField)elements.get(elements.size()-1)).setId("mistake");
				  }
			  }
		  }	
			
		}
		System.out.println(totalQuestions+"cccccccc");
		score.setText("current score "+totalRight+"/"+totalQuestions+ "\n" +" total solved "+totalSolved); 
		if(totalRight==totalQuestions)
			locking.setStyle(
					"-fx-background-color: transparent; -fx-background-size: 35px; -fx-background-repeat: no-repeat;-fx-background-image: url('unlocked.png');");
	}
	
	//------------------------------------------problems section---------------------------------------------------------------		
	//------------------------------------settings----------------------------------------------
	public static VBox settingsPage() {
		   VBox vbox = new VBox(numberOfSets(),numberSizeOPtion(),problemLength(),numberProblemsPerSet());
        return vbox; 
	}
	
	public static HBox numberProblemsPerSet() {
		 RadioButton radioButton1 = new RadioButton("5");
	     RadioButton radioButton2 = new RadioButton("6");
	     RadioButton radioButton3 = new RadioButton("7");
	     RadioButton radioButton4 = new RadioButton("8");
	     RadioButton radioButton5 = new RadioButton("9");    
	     RadioButton radioButton6 = new RadioButton("10");
	     RadioButton radioButton7 = new RadioButton("11");
	     RadioButton radioButton8 = new RadioButton("12");
	     RadioButton radioButton9 = new RadioButton("13");
	     RadioButton radioButton10 = new RadioButton("14");  
	     RadioButton radioButton11 = new RadioButton("15");  
	     
	     ToggleGroup radioGroup = new ToggleGroup();

	     radioButton1.setToggleGroup(radioGroup);
	     radioButton2.setToggleGroup(radioGroup);
	     radioButton3.setToggleGroup(radioGroup);
	     radioButton4.setToggleGroup(radioGroup);
	     radioButton5.setToggleGroup(radioGroup);
	     radioButton6.setToggleGroup(radioGroup);
	     radioButton7.setToggleGroup(radioGroup);
	     radioButton8.setToggleGroup(radioGroup);
	     radioButton9.setToggleGroup(radioGroup);
	     radioButton10.setToggleGroup(radioGroup);
	     radioButton11.setToggleGroup(radioGroup);
	     
	     radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()  
	     { 
	         public void changed(ObservableValue<? extends Toggle> ob,  
	                                                 Toggle o, Toggle n) 
	         { 
	             RadioButton rb = (RadioButton)radioGroup.getSelectedToggle(); 
	             if (rb != null) { 
	                 String s = rb.getText(); 
	                 // change the label 
	                 numberOfProblemsperSet=Integer.parseInt(s);
	             } 
	         } 
	     }); 

	                         
	     Label title = new Label("problems per set        : ");
	     HBox hbox = new HBox(title,radioButton1, radioButton2, radioButton3, radioButton4,radioButton5,radioButton6, radioButton7, radioButton8, radioButton9,radioButton10,radioButton11);
	     
	     return hbox;

	}
	
	public static HBox numberSizeOPtion() {
	 RadioButton radioButton1 = new RadioButton("1");
     RadioButton radioButton2 = new RadioButton("2");
     RadioButton radioButton3 = new RadioButton("3");
     RadioButton radioButton4 = new RadioButton("4");
     RadioButton radioButton5 = new RadioButton("5");    
     
     ToggleGroup radioGroup = new ToggleGroup();

     radioButton1.setToggleGroup(radioGroup);
     radioButton2.setToggleGroup(radioGroup);
     radioButton3.setToggleGroup(radioGroup);
     radioButton4.setToggleGroup(radioGroup);
     radioButton5.setToggleGroup(radioGroup);

     radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()  
     { 
         public void changed(ObservableValue<? extends Toggle> ob,  
                                                 Toggle o, Toggle n) 
         { 
             RadioButton rb = (RadioButton)radioGroup.getSelectedToggle(); 
             if (rb != null) { 
                 String s = rb.getText(); 
                 // change the label 
                 numberSize=Integer.parseInt(s);
             } 
         } 
     }); 

     
     Label title = new Label("max size of each number : ");
     HBox hbox = new HBox(title,radioButton1, radioButton2, radioButton3, radioButton4,radioButton5);
     
     return hbox;

}
	
	public static HBox numberOfSets() {
		 RadioButton radioButton1 = new RadioButton("1");
	     RadioButton radioButton2 = new RadioButton("2");
	     RadioButton radioButton3 = new RadioButton("3");
	     RadioButton radioButton4 = new RadioButton("4");  
	     
	     ToggleGroup radioGroup = new ToggleGroup();

	     radioButton1.setToggleGroup(radioGroup);
	     radioButton2.setToggleGroup(radioGroup);
	     radioButton3.setToggleGroup(radioGroup);
	     radioButton4.setToggleGroup(radioGroup);

	     radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()  
	     { 
	         public void changed(ObservableValue<? extends Toggle> ob,  
	                                                 Toggle o, Toggle n) 
	         { 
	             RadioButton rb = (RadioButton)radioGroup.getSelectedToggle(); 
	             if (rb != null) { 
	                 String s = rb.getText(); 
	                 // change the label 
	                 numberOfsets=Integer.parseInt(s);
	             } 
	         } 
	     }); 

	                            
	     Label title = new Label("Problem sets in page    : ");
	     HBox hbox = new HBox(title,radioButton1, radioButton2, radioButton3, radioButton4);
	     
	     return hbox;

	}
	
	public static HBox problemLength() {
	    RadioButton radioButton1 = new RadioButton("2");
     RadioButton radioButton2 = new RadioButton("3");
     RadioButton radioButton3 = new RadioButton("4");
     RadioButton radioButton4 = new RadioButton("5");
     RadioButton radioButton5 = new RadioButton("6");    
     RadioButton radioButton6 = new RadioButton("7");    
     RadioButton radioButton7 = new RadioButton("8");
     RadioButton radioButton8 = new RadioButton("9");
     RadioButton radioButton9 = new RadioButton("10");
     
     ToggleGroup radioGroup = new ToggleGroup();

     radioButton1.setToggleGroup(radioGroup);
     radioButton2.setToggleGroup(radioGroup);
     radioButton3.setToggleGroup(radioGroup);
     radioButton4.setToggleGroup(radioGroup);
     radioButton5.setToggleGroup(radioGroup);
     radioButton6.setToggleGroup(radioGroup);
     radioButton7.setToggleGroup(radioGroup);
     radioButton8.setToggleGroup(radioGroup);
     radioButton9.setToggleGroup(radioGroup);
     
     radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()  
     { 
         public void changed(ObservableValue<? extends Toggle> ob,  
                                                 Toggle o, Toggle n) 
         { 
             RadioButton rb = (RadioButton)radioGroup.getSelectedToggle(); 
             if (rb != null) { 
                 String s = rb.getText(); 
                 // change the label 
                 questionSize=Integer.parseInt(s); 
             } 
         } 
     }); 
    
     Label title = new Label("size of each problem    : ");
     HBox hbox = new HBox(title,radioButton1, radioButton2, radioButton3, radioButton4,radioButton5,radioButton6,radioButton7,radioButton8,radioButton9);
     
     return hbox;

}
//------------------------------------settings--------------------------------------------------------------------------------	
	
//------------------------------------start page------------------------------------------------------------------------------
	private GridPane createStartPage() {
		GridPane gridPane = new GridPane();
		gridPane.setPrefHeight(700);
		gridPane.setPrefWidth(1700);
		gridPane.setPadding(new Insets(2));
		gridPane.setHgap(50);
		gridPane.setVgap(50);
		gridPane.setStyle(
				"-fx-background-size: 1700px; -fx-background-repeat:no-repeat; -fx-background-image: url('mentalArithmetic.jpg')");
		gridPane.setAlignment(Pos.CENTER);

		startButton.setPrefHeight(60);
		startButton.setDefaultButton(true);
		startButton.setPrefWidth(300);
		startButton.setId("record-sales");
		
		settingsButton.setPrefHeight(60);
		settingsButton.setDefaultButton(true);
		settingsButton.setPrefWidth(300);
		settingsButton.setId("record-sales");
		
		gridPane.add(new HBox(startButton,settingsButton), 0, 11, 2, 1);
		GridPane.setHalignment(startButton, HPos.CENTER);
		GridPane.setMargin(startButton, new Insets(20, 0, 20, 0));
		
		return gridPane;
	}
//------------------------------------start page------------------------------------------------------------------------------	
//--------------------------------------------controls section----------------------------------------------------------------	
	public static VBox generateControls() {
		VBox controls = new VBox();
		
		score.setId("label-control");
		score.setPrefWidth(300);
		score.setPrefHeight(300);
		
		
		verify.setId("record-sales");
		verify.setMaxHeight(10);	
		verify.setPrefWidth(300);
		
		next.setId("record-sales");
		next.setMaxHeight(10);	
		next.setPrefWidth(260);
		
		
		locking.setStyle(
				"-fx-background-color: transparent; -fx-background-size: 35px; -fx-background-repeat: no-repeat;-fx-background-image: url('locked.png');");
		
		controls.getChildren().addAll(score,verify,new HBox(next,locking));
		return controls;
	}
//--------------------------------------------controls section----------------------------------------------------------------		
	
}
