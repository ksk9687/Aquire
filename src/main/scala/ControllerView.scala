import scala.swing._
import scala.swing.Swing._
import java.awt.Color
import java.awt.Font
import GridBagPanel._
import javax.swing.table._
import javax.swing.JToggleButton
import scala.swing.event._

class ControllerView extends GridBagPanel {

    object Private extends FlowPanel {
        preferredSize = (600, 30)
        val nm = new Label("Name")
        nm.font = new Font("monospace", Font.BOLD, 16)
        contents += nm
        val cash = new Label("cash")
        contents += cash
        cash.font = new Font("monospace", Font.BOLD, 16)
        val sw = new ToggleButton("タイルの表示")
        sw.selected = UIAdmin.showPrivateData
        contents += sw
        background = Color.orange

        listenTo(sw)
        reactions += {
            case ButtonClicked(x) => 
            	UIAdmin.setPrivateMode (x.peer.isSelected)
        }

        override def paint(g : Graphics2D) = {
            update
            super.paint(g)
        }
        def update() = {
        	if(UIAdmin.showPrivateData){
        		sw.selected = true
        	}else{
        		sw.selected = false
        	}
            nm.text = GameAdmin.players(GameAdmin.turn).name 
            cash.text = if (UIAdmin.showPrivateData) GameAdmin.players(GameAdmin.turn).money.toString else "*****"
        }
    }
    object Stocks extends ScrollPane {
        preferredSize = (150, 220)
        background = Color.gray

        val tableModel = new DefaultTableModel() {
            override def isCellEditable(r : Int, c : Int) = {
                false
            }
        }
        val t = new Table()
        t.peer.setModel(tableModel)
        tableModel.addColumn("Hotel")
        tableModel.addColumn("Stocks")
        Aquire.Hotels.foreach(n => tableModel.addRow(Array[Object]()))
        t.peer.getColumnModel().getColumn(0).setPreferredWidth(120);
        contents = t
        t.rowHeight = 28
        override def paint(g : Graphics2D) = {
            update
            super.paint(g)
        }
        def update() = {
            var i = 0
            for (name <- Aquire.Hotels) {
                tableModel.setValueAt(name, i, 0)
                tableModel.setValueAt(GameAdmin.players(GameAdmin.turn).stocks(name), i, 1)
                i += 1
            }
        }
    }
    object Act extends Panel {
        preferredSize = (450, 220)
        background = new Color(0, 0, 0)
	
        val TILEWIDTH = 50
        val TILEMARGIN = 15

        override def paint(g : Graphics2D) = {
            g.setColor(background)
            g.fillRect(0, 0, preferredSize.width, preferredSize.height)

            var xm = TILEMARGIN
            var x = 0
            val y = 0
            if (UIAdmin.showPrivateData) for (tile <- GameAdmin.players(GameAdmin.turn).tiles) {
                g.setColor(Aquire.HotelColor(Aquire.NotHotelName))
                g.fill3DRect((x) * TILEWIDTH + 1 + xm, (y) * TILEWIDTH + 1 + TILEMARGIN, TILEWIDTH - 2, TILEWIDTH - 2, true)
                g.setColor(Color.black)
                g.setFont(new Font("monospace", Font.BOLD, 16))
                val s = Aquire.getTileName(tile)
                g.drawString(s, (x) * TILEWIDTH + 22 - s.length * 5 + xm, (y) * TILEWIDTH + 30 + TILEMARGIN)
                if(tile == UIAdmin.selectedTile){
                	g.setColor(Color.red)
                	g.drawString(s, (x) * TILEWIDTH + 22 - s.length * 5 + xm, (y) * TILEWIDTH + 30 + TILEMARGIN)
                }
                x += 1
                xm += TILEMARGIN
            }
            
        }
        
        listenTo(mouse.clicks, mouse.moves)
        
        reactions += {
        	case e: MouseMoved  => 
        		UIAdmin.tileFeedback = (-1,-1)
        		if (UIAdmin.showPrivateData){
        			val px:Int = e.point.x / (TILEWIDTH + TILEMARGIN)
        			val mpx:Int = e.point.x % (TILEWIDTH + TILEMARGIN)
        			if (px < GameAdmin.players(GameAdmin.turn).tiles.size) if (TILEMARGIN <= mpx && TILEMARGIN <= e.point.y && e.point.y <=  (TILEWIDTH + TILEMARGIN)){
        				UIAdmin.tileFeedback = GameAdmin.players(GameAdmin.turn).tiles(px);
        			}
        		}	
        	case e: MouseClicked  => 
        		if (UIAdmin.showPrivateData){
        			val px:Int = e.point.x / (TILEWIDTH + TILEMARGIN)
        			val mpx:Int = e.point.x % (TILEWIDTH + TILEMARGIN)
        			if (px < GameAdmin.players(GameAdmin.turn).tiles.size) if (TILEMARGIN <= mpx && TILEMARGIN <= e.point.y && e.point.y <=  (TILEWIDTH + TILEMARGIN)){
        				if(UIAdmin.selectedTile ==  GameAdmin.players(GameAdmin.turn).tiles(px)){
        					UIAdmin.selectedTile =  (-1,-1);
        				}else{
        					UIAdmin.selectedTile = GameAdmin.players(GameAdmin.turn).tiles(px);
        					GameAdmin.putTile()
        				}
        			}
        		}
        }
    }
    preferredSize = (600, 250)
    background = Color.orange

    val c = new Constraints
    c.fill = Fill.Both

    c.gridwidth = 12;
    c.gridheight = 1;
    c.gridx = 0;
    c.gridy = 0;
    layout(Private) = c

    c.gridwidth = 3;
    c.gridheight = 5;
    c.gridx = 0;
    c.gridy = 1;
    layout(Stocks) = c

    c.gridwidth = 9;
    c.gridheight = 4;
    c.gridx = 3;
    c.gridy = 1;
    layout(Act) = c
}

class Stocks extends ScrollPane {
    preferredSize = (150, 250)
}