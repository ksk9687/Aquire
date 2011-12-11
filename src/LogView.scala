import scala.swing._
import scala.swing.Swing._
import java.awt.Color
import javax.swing._;
import java.util.Vector;
import javax.swing.border._;

class LogView  extends ScrollPane{
	preferredSize = (300,700)
	background = Color.white
	
	val lv =new ListView()
	lv.peer.setModel(Logger.viewlist)
	lv.peer.setFixedCellHeight(25)
	contents = lv
	
	border = new LineBorder(Color.black, 1, false);
}