import javax.swing.DefaultListModel
import javax.swing.ListModel
import scala.collection.mutable.LinkedList

object Logger {
	object Level extends Enumeration{
		val PROGRAM,SYSTEM,GAME = Value
	}
	val viewLevel = Set(Level.PROGRAM,Level.SYSTEM,Level.GAME)
	var viewlist = new DefaultListModel
	var messages = new LinkedList[(Level.Value,String)]
	
	def log(mes:String):Unit = {
		messages = (Level.GAME,mes) +: messages
		update()
	}
	def log(level:Level.Value,mes:String):Unit = {
		messages = (level,mes) +: messages
		update()
	}
	
	def update(){
		viewlist.removeAllElements
		messages.filter(t => viewLevel.contains(t._1)).map(_._2 ).foreach(s =>viewlist.addElement(s))
	}
}