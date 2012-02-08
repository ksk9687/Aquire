import scala.swing._
import scala.swing.Swing._
import GridBagPanel._
import scala.swing.event._
import java.awt.Color
import javax.swing._

class MergeView(val winner:Hotel,val loser:Hotel,val player : Player)  extends Frame {
	resizable = false
    title = player.name + "さん " + loser.name +"株式処分" + "(" + player.stocks(loser.name) +")"
    peer.setLocationRelativeTo(null)
    val w = 200;
	
	peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE )

	
	var selectList  = {
		for(x <- 0 to player.stocks(loser.name)) yield x
    }
	var selectList2  = {
		for(x <- 0 to player.stocks(loser.name) if x%2 ==0) yield x
    }
	val stock1 = new ComboBox(selectList) {
        preferredSize = (w, 50)
        selection.index = selectList.size - 1
    }
	val stock2 = new ComboBox(selectList) {
        preferredSize = (w, 50)
    }
	val stock3 = new ComboBox(selectList2) {
        preferredSize = (w, 50)
    }
	val message = new Label("") {
        preferredSize = (w, 30)
        foreground = Color.red
    }
	val btn = new Button("決定") {
        preferredSize = (w + 100, 50)
    }
	
contents = new GridBagPanel {

        val c = new Constraints
        c.fill = Fill.Both

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        layout(new Label("売却:@"+loser.price)) = c
        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        layout(stock1) = c
        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        layout(new Label("交換:"+winner.name+" ("+"価格:"+winner.price+",残:"+winner.stock+")")) = c
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 1;
        layout(stock3) = c

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 2;
        layout(new Label("保持")) = c
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 2;
        layout(stock2) = c
        
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        layout(message) = c
        
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 4;
        layout(btn) = c
    }
    listenTo(btn,stock1.selection,stock2.selection,stock3.selection)
    reactions += {
        case ButtonClicked(x) =>
            if (check()) {
            	val sell = stock1.selection.item
            	val exchange = stock3.selection.item
            	val hold = stock3.selection.item
            	
            	player.addStock(loser.name,sell * -1);
            	player.money += loser.price * sell;
            	
            	player.addStock(loser.name,exchange * -1);
            	player.addStock(winner.name,exchange / 2);
            	
                GameAdmin.TurnState = Aquire.turnState.MERGE;
                GameAdmin.turn  = (GameAdmin.turn + 1) % GameAdmin.players.size
                UIAdmin.setPrivateMode(false);
                close();
            }
        case SelectionChanged(x) =>
        	btn.enabled = check()
    }
	    
	def check() : Boolean = {
    	val sum = stock1.selection.item + stock2.selection.item + stock3.selection.item;
    	if(sum != player.stocks(loser.name)){
    		message.text = "処分する株式数が合いません。"
    		return false;
    	}
    	if(stock2.selection.item / 2 > winner.stock ){
    		message.text = winner.name + "の残り株式数が足りません。"
    		return false;
    	}

    	message.text = ""
        return true;
    }
	        
}