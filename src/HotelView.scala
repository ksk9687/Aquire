import scala.swing._
import scala.swing.Swing._
import java.awt.Color
import java.awt.Font
import javax.swing.border._;


class HotelView extends Panel{
	preferredSize = (200,700)
	background = Color.darkGray
	border = new LineBorder(Color.black, 2, false);
	
	val rowHeight = 88
	val rowMergin = 10
	val tileWidth = 25
	val tileBorder = 5
	
	val boxWidth = 145
	val boxBorder = 5
	
	override def paint(g:Graphics2D) = {
		g.setColor(background)
		g.fillRect(0,0,preferredSize.width ,preferredSize.height)
		
		var xofs = 10
		var yofs = 10
		for(hn <- Aquire.Hotels){
			val hotel = GameAdmin.hotels (hn)
			val c = Aquire.HotelColor(hn)
			g.setColor(c)
			g.fillRect(xofs, yofs, tileWidth, rowHeight)
			
			if(!hotel.canEstablish){
				g.setColor(Color.white)
				g.fillRect(xofs + tileBorder, yofs + tileBorder, tileWidth-tileBorder *2, rowHeight-tileBorder*2)
				g.setColor(new Color(c.getRed,c.getGreen,c.getBlue,30))
				g.fillRect(xofs + tileBorder, yofs + tileBorder, tileWidth-tileBorder *2, rowHeight-tileBorder*2)
			
				if(hotel.tiles >= Aquire.SafeSize ){
					g.setColor(c)
					g.setFont(new Font("monospace" , Font.PLAIN , 20))
					g.drawString("★",xofs + tileWidth / 2 - 8, yofs + rowHeight / 2 + 8)
				}
			}
			var x = xofs + tileWidth + xofs
			g.setColor(c)
			g.fillRect(x, yofs , boxWidth, rowHeight)
			g.setColor(Color.white)
			g.fillRect(x + boxBorder, yofs + boxBorder , boxWidth - boxBorder *2 , rowHeight - boxBorder *2)
			g.setColor(new Color(c.getRed,c.getGreen,c.getBlue,30))
			g.fillRect(x + boxBorder, yofs + boxBorder , boxWidth - boxBorder *2 , rowHeight - boxBorder *2)
			x = x + boxBorder
			g.setColor(Color.black)
			g.setFont(new Font("monospace" , Font.BOLD , 16))
			g.drawString(hn, x + 5, yofs + 25)
			
			g.setFont(new Font("monospace" , Font.PLAIN , 14))
			if(hotel.price == 0){
				g.drawString("Unlisted" , x + 5, yofs + 50)
			}else{
				g.drawString("@" + hotel.price, x + 5, yofs + 50)
			}
			g.drawString(hotel.stock+" / 25", x + 80, yofs + 50)
			g.setColor(Color.black)
			g.drawRect(x + 5, yofs + 60, 100, 15)
			
			g.setColor(c)
			var l = hotel.tiles.toDouble / Aquire.BigSize 
			if (l > 1) l = 1
			g.fillRect(x + 6, yofs + 61, (98.0 * l).toInt, 14)
			g.setColor(Color.white)
			g.fillRect(x + 6 + (98.0 * l).toInt, yofs + 61, 98 - (98.0 * l).toInt, 14)
			g.setColor(Color.black)
			g.drawLine(x + 6 + (98.0 * (Aquire.SafeSize.toDouble/ Aquire.BigSize )).toInt, yofs + 61, x + 6 + (98.0 * (Aquire.SafeSize.toDouble/ Aquire.BigSize )).toInt, yofs + 74)
		
			g.drawString(hotel.tiles.toString, x + 113, yofs + 72)
			
			yofs += rowHeight + rowMergin
		}
	}
	
}