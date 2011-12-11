import scala.swing._
import scala.swing.Swing._
import GridBagPanel._
import scala.swing.event._
import java.awt.Color
import javax.swing._

class BuyStocks(val player : Player) extends Frame {
    resizable = false
    title = player.name + "さん 株式購入"
    peer.setLocationRelativeTo(null)
    val w = 300;
    val w2 = 150;
    
    peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    	
    var selectableList : List[String] = {
        Aquire.Hotels.toList.filter(x => GameAdmin.hotels(x).buyableStocks() > 0)
    }
    var selectList : List[String] = {
        "購入しない" :: selectableList.map(
            x => x + " (" + "価格:" + GameAdmin.hotels(x).price + ",残:" + GameAdmin.hotels(x).stock + ")")
    }

    val stock1 = new ComboBox(selectList) {
        preferredSize = (w, 50)
    }
    val stock2 = new ComboBox(selectList) {
        preferredSize = (w, 50)
    }
    val stock3 = new ComboBox(selectList) {
        preferredSize = (w, 50)
    }

    val money = new Label(player.money.toString) {
        preferredSize = (w2, 30)
        horizontalAlignment = Alignment.Right 
    }
    val kakaku = new Label(0.toString) {
        preferredSize = (w2, 30)
        horizontalAlignment = Alignment.Right 
    }
    val nokori = new Label(player.money.toString) {
        preferredSize = (w2, 30)
        horizontalAlignment = Alignment.Right 
    }
    
    val message = new Label("") {
        preferredSize = (w, 30)
        foreground = Color.red
    }
    val btn = new Button("決定") {
        preferredSize = (200, 50)
    }
    contents = new GridBagPanel {

        val c = new Constraints
        c.fill = Fill.Both

        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        layout(stock1) = c

        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        layout(stock2) = c

        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 2;
        layout(stock3) = c

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        layout(new Label("所持金")) = c
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 3;
        layout(money) = c
        
        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 4;
        layout(new Label("購入金額")) = c
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 4;
        layout(kakaku) = c
        
        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 5;
        layout(new Label("残金")) = c
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 5;
        layout(nokori) = c
        
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 6;
        layout(message) = c
        
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 7;
        layout(btn) = c
    }

    listenTo(btn,stock1.selection,stock2.selection,stock3.selection)
    reactions += {
        case ButtonClicked(x) =>
            if (check()) {
                buy(stock1);
                buy(stock2);
                buy(stock3);
                close();
                GameAdmin.TurnState = Aquire.turnState.END;
            }
        case SelectionChanged(x) =>
        	btn.enabled = check()
    }

    def buy(c : ComboBox[String]) = {
        c.selection.index match {
            case 0 =>
            case x if x <= selectableList.size =>
                player.money -= GameAdmin.hotels(selectableList(x - 1)).price;
                player.addStock(selectableList(x - 1), 1);
            case _ =>
                throw new Exception();
        }
    }

    def price(c : ComboBox[String]):Int = {
        c.selection.index match {
            case 0 => 0
            case x if x <= selectableList.size =>
                GameAdmin.hotels(selectableList(x - 1)).price;
            case _ =>
                throw new Exception();
        }
    }
    def stock(c : ComboBox[String]):Int = {
        c.selection.index match {
            case 0 => 100
            case x if x <= selectableList.size =>
                GameAdmin.hotels(selectableList(x - 1)).stock ;
            case _ =>
                throw new Exception();
        }
    }
    def check() : Boolean = {
    	val cost = price(stock1) + price(stock2) + price(stock3);
    	if(cost > player.money){
    		message.text = "所持金が足りません。"
    		return false;
    	}
    	var s1 = stock(stock1) - 1;
    	var s2 = stock(stock2) - 1;
    	var s3 = stock(stock3) - 1;
    	if(stock1.selection.index == stock2.selection.index){
    		s2 -= 1;
    	}
    	if(stock1.selection.index == stock3.selection.index){
    		s3 -= 1;
    	}
    	if(stock2.selection.index == stock3.selection.index){
    		s3 -= 1;
    	}
    	
    	if(s1 < 0){
    		message.text = stock1.selection.item +"残り株数が足りません。"
    		return false;
    	}
    	if(s2 < 0){
    		message.text = stock2.selection.item +"残り株数が足りません。"
    		return false;
    	}
    	if(s3 < 0){
    		message.text = stock3.selection.item +"残り株数が足りません。"
    		return false;
    	}
    	
    	message.text = ""
        return true;
    }
}