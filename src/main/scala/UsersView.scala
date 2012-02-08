import scala.swing._
import scala.swing.Swing._
import java.awt.Color
import javax.swing.table._

class UsersView  extends ScrollPane{
	preferredSize = (1100,20 + 20 * GameAdmin.player )
	background = Color.blue
	val Cols:List[String] = List[String]("名前","資金") ::: Aquire.Hotels.toList
	
	val tableModel = new DefaultTableModel(){
		override def isCellEditable(r:Int,c:Int) = {
			false
		}
	}
	val t = new Table()
	t.peer.setModel(tableModel)
	contents = t
	t.rowHeight = 20
	
	for(c <- Cols){
		tableModel.addColumn(c)
	}
	GameAdmin.players .foreach(p => tableModel.addRow(Array[java.lang.Object]()))
	override def paint(g:Graphics2D) = {
		super.paint(g)
		update
	}
	
	
	def update() = {
		var i = 0
		for(p <- GameAdmin.players){
			tableModel.setValueAt(p.name, i, 0)
			tableModel.setValueAt(p.money, i, 1)
			for(j <- 2 to tableModel.getColumnCount-1){
				tableModel.setValueAt(p.stocks(tableModel.getColumnName(j)), i, j)
			}
			i += 1
		}
	}
}