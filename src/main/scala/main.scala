import scala.swing._
import scala.swing.Swing._
import GridBagPanel._
import swing._
import event._
import javax.swing.{Timer => SwingTimer, AbstractAction}
import java.awt.event.{ActionEvent}
  
object main extends SimpleSwingApplication {
	
	
    def top : Frame = new MainFrame() {
		resizable = false
		title = Aquire.Name 
    	
		Logger.log(Logger.Level.SYSTEM,"起動")
		
		GameAdmin.initialize
		contents = new GridBagPanel{
			val c = new Constraints
			c.fill = Fill.Both
			
			c.gridwidth = 20;
			c.gridheight = 75;
			c.gridx = 0;
			c.gridy = 0;
			val hotel = new HotelView
			layout(hotel) = c
			
			
			c.gridwidth = 60;
			c.gridheight = 45;
			c.gridx = 20;
			c.gridy = 0;
			val map = new MapView
			layout(map) = c
			
			c.gridwidth = 30;
			c.gridheight = 75;
			c.gridx = 80;
			c.gridy = 0;
			val log = new LogView
			layout(log) = c
			
			
			c.gridwidth = 60;
			c.gridheight = 30;
			c.gridx = 20;
			c.gridy = 45;
			val ctrl = new ControllerView
			layout(ctrl) = c
			
			
			c.gridwidth = 110;
			c.gridheight = 15;
			c.gridx = 0;
			c.gridy = 75;
			val user = new UsersView
			layout(user) = c
			
			Logger.log(Logger.Level.SYSTEM,"表示")
		}
		        
	    val timer = new SwingTimer(100, new AbstractAction() {
	      override def actionPerformed(e: ActionEvent) {
	     	  GameAdmin.loop()
	          repaint()
	      }
	    }).start
    }


}
