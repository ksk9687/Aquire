import scala.swing._
import scala.swing.Swing._
import java.awt.Color
import java.awt.Font
import javax.swing.border._;
class MapView  extends Panel{
	preferredSize = (600,450)
	background = Color.black
	border = new LineBorder(Color.black, 2, false);
	val TILEWIDTH = 50
	
	override def paint(g:Graphics2D) = {
		g.setColor(background)
		g.fillRect(0,0,preferredSize.width ,preferredSize.height)
		for(x <- 0 to Aquire.MapX-1 ){
			for(y <- 0 to Aquire.MapY-1 ){
				GameAdmin.map.status(x)(y) match{
					case None => 
						g.setColor(Aquire.EmptyColor)
						g.fillRect((x) *TILEWIDTH +1 , (y) * TILEWIDTH + 1, TILEWIDTH-2, TILEWIDTH-2)
						g.setColor(Color.white)
						g.setFont(new Font("monospace" , Font.BOLD , 16))
						val s = Aquire.getTileName(x, y)
						g.drawString(s, (x) *TILEWIDTH + 22 - s.length*5,  (y) * TILEWIDTH + 30)
					case Some(name) if name == Aquire.NotHotelName => 
						g.setColor(Aquire.HotelColor(name))
						g.fill3DRect((x) *TILEWIDTH +1 , (y) * TILEWIDTH + 1, TILEWIDTH-2, TILEWIDTH-2,true)
						g.setColor(Color.black)
						g.setFont(new Font("monospace" , Font.BOLD , 16))
						val s = Aquire.getTileName(x, y)
						g.drawString(s, (x) *TILEWIDTH + 22 - s.length *5,  (y) * TILEWIDTH + 30)
					case Some(name) => 
						g.setColor(Aquire.HotelColor(name))
						g.fill3DRect((x) *TILEWIDTH +1 , (y) * TILEWIDTH + 1, TILEWIDTH-2, TILEWIDTH-2,true)
						g.setColor(Aquire.HotelColor(name).darker)
						g.fillRect((x) *TILEWIDTH +1 , (y) * TILEWIDTH + 30, TILEWIDTH-2, 16)
						g.setColor(Color.white)
						g.setFont(new Font("Sans" , Font.BOLD , 10))
						g.drawString(name.substring(0,5), (x) *TILEWIDTH + 7,  (y) * TILEWIDTH + 41)		
				}
			}
		}
		val c = Aquire.HotelColor(Aquire.NotHotelName);
		g.setColor(new Color(c.getRed,c.getGreen,c.getBlue,0))
		if (UIAdmin.showPrivateData) for((x,y) <- GameAdmin.players(GameAdmin.turn).tiles ){
				g.fill3DRect((x) *TILEWIDTH +1 , (y) * TILEWIDTH + 1, TILEWIDTH-2, TILEWIDTH-2,true)
		}
		
		if (UIAdmin.showPrivateData &&  UIAdmin.tileFeedback != UIAdmin.nullTile ){
			g.setColor(new Color(c.getRed,c.getGreen,c.getBlue,100))
			val (x,y) = UIAdmin.tileFeedback
			g.fill3DRect((x) *TILEWIDTH +1 , (y) * TILEWIDTH + 1, TILEWIDTH-2, TILEWIDTH-2,true)
		}
		
		if (UIAdmin.showPrivateData &&  UIAdmin.selectedTile != UIAdmin.nullTile){
			g.setColor(Color.red)
			val (x,y) = UIAdmin.selectedTile
			g.setFont(new Font("monospace" , Font.BOLD , 16))
			val s = Aquire.getTileName(x, y)
			g.drawString(s, (x) *TILEWIDTH + 22 - s.length *5,  (y) * TILEWIDTH + 30)
		}
		
	}
}