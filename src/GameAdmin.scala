import scala.collection.mutable.ArrayBuffer
import javax.swing._
import boardGame._

object GameAdmin {
	var State = Aquire.state.SETUP
	var TurnState = Aquire.turnState.BEGIN
	var gTurn = 0
	
	var turn = 0
	var map = new AquireMap
	var hotels = Map[String,Hotel]()
	var player = 0
	var players = new ArrayBuffer[Player]()
	var cardPool = new SimpleDeck[(Int,Int)]
	var cardTrush = new SimpleDeck[(Int,Int)]
	
	var mergeWin:String = null;
	var mergeLose:Array[Object] = null;
	var mergeDisap:String = null;
	var mergeTile = (0,0);
	
	def initialize() = {
		State = Aquire.state.SETUP
		map = new AquireMap
		hotels = {
			var m = Map[String,Hotel]()
			Aquire.Hotels.foreach(name => m = m + (name -> new Hotel(name) ))
			m
		}
		cardPool.clear
		for(x <- 0 to Aquire.MapX -1){
			for(y <- 0 to Aquire.MapY -1){
				cardPool.pushTop((x,y))
			}
		}
		cardPool.shuffle
		cardTrush.clear
		turn = 0
		
		val sel = Array[Object]("1","2","3","4","5","6")
		val num = JOptionPane.showInputDialog(
				null , "プレイ人数を選択してください" , "" ,
				JOptionPane.INFORMATION_MESSAGE , null , sel , sel(4)	);
		if(num == null){
			System.exit(0);
		}
		player = num.toString.toInt
			Logger.log(player + "人　プレイ")
		players.clear()
		// TODO
		for(i <- 1 to player){
			val name = JOptionPane.showInputDialog(
				null , "名前" , "" ,
				JOptionPane.INFORMATION_MESSAGE );
			players += new Player(name)
			Logger.log("Player "+name + " を追加")
		}
		
		def shuffle[T](a: ArrayBuffer[T]) = {
		  for (i <- 1 until a.size reverse) {
		    val j = util.Random nextInt (i + 1)
		    val t = a(i)
		    a(i) = a(j)
		    a(j) = t
		  }
		  a
		}
		players = shuffle(players)
		var i = 0;
		while(i < Aquire.InitTiles){
			val t = cardPool.draw
			map.putMode(t) match{
				case NotChain() =>
					i += 1;
					map.putTile(t)
				case _ =>
					cardPool.pushTop(t);
					cardPool.shuffle;
			}
		}
		Logger.log("Setup 終了")
		players.foreach(_.fillTiles())
		State = Aquire.state.PLAYER
		TurnState = Aquire.turnState.TILE
	}
	
	def loop():Unit = {
		val turnPlayer = players(turn)
		if(State.equals(Aquire.state.END)) return
		TurnState match{
			case Aquire.turnState.BEGIN =>
				turnPlayer.refleshTiles()
				JOptionPane.showMessageDialog(
					null ,turnPlayer.name + " さんのターンです。"  , turnPlayer.name + " さん" ,
					JOptionPane.INFORMATION_MESSAGE
				);
				if(turnPlayer.tiles .exists(t=> !map.canNottPut(t))){
					TurnState = Aquire.turnState.TILE
				}else{
					JOptionPane.showMessageDialog(
						null ,"置くことのできるタイルがありません。購入フェーズに移ります。"  , turnPlayer.name + " さん" ,
						JOptionPane.INFORMATION_MESSAGE
					);
					TurnState = Aquire.turnState.BUY
				}
			case Aquire.turnState.TILE =>
			case Aquire.turnState.MERGE =>
				if(turn == gTurn){
					if(mergeLose.size > 0){
						var selec:Object = null;
						if(mergeLose.size == 1){
							selec = mergeLose(0)
						}
						while(selec == null){
							selec = JOptionPane.showInputDialog(
								null , "消滅させるチェーン名を選択してください" , players(gTurn).name + " さん" ,
								JOptionPane.INFORMATION_MESSAGE , null , mergeLose , mergeLose(0)	);
						}
						mergeDisap = selec.toString
						mergeLose = mergeLose.filter(_ != selec);
						stockHolderBonuse(hotels(mergeDisap));
						disapHotel(mergeWin,mergeDisap);
					}else{
						map.putTile(mergeTile,mergeWin);
						TurnState = Aquire.turnState.BUY;
					}
				}else{
					disapHotel(mergeWin,mergeDisap);
				}
			case Aquire.turnState.BUY =>
				val w = new BuyStocks(turnPlayer);
				w.visible = true;
				TurnState = Aquire.turnState.WAIT ;
			case Aquire.turnState.WAIT =>
			case Aquire.turnState.END =>{
				if (canEnd) {
						val message = Array(
							"ゲーム終了条件を満たしています。" ,
							"ゲームを終了しますか？"
						);
						val value = JOptionPane.showConfirmDialog(
							null , message , turnPlayer.name + " さん" , JOptionPane.YES_NO_OPTION
						);
						if (value == JOptionPane.YES_OPTION){
							State = Aquire.state.END
							gameEnd()
							return
						}
				}
				turnPlayer.fillTiles();
				UIAdmin.setPrivateMode(false)
				turn = (turn + 1) % (players.size)
				TurnState = Aquire.turnState.BEGIN
			}
		}
	}
	def putTile():Unit = {
		if(!TurnState.equals(Aquire.turnState.TILE)) return
		val turnPlayer = players(turn)
		val tile = UIAdmin.selectedTile;
		map.putMode(tile) match{
			case Establish(list) => {
				var selec:Object = null;
				val l :Array[Object] = list.toArray.asInstanceOf[Array[Object]];
				while(selec == null){
					selec = JOptionPane.showInputDialog(
							null , "設立するチェーン名を選択してください" , players(turn).name + " さん" ,
							JOptionPane.INFORMATION_MESSAGE , null , l , l.first	);
				}
				val win = selec.toString
				Logger.log(win + "を設立しました。")
				map.putTile(UIAdmin.selectedTile,win);
				turnPlayer.addStock(win,1);
				Logger.log(win + "の株券を一枚獲得")
			}
			case ChooseMerge(list) => {
				var selec:Object = null;
				var l :Array[Object] = list.toArray.asInstanceOf[Array[Object]];
				while(selec == null){
					selec = JOptionPane.showInputDialog(
							null , "残すチェーン名を選択してください" , players(turn).name + " さん" ,
							JOptionPane.INFORMATION_MESSAGE , null , l , l.first	);
				}
				mergeWin = selec.toString
				mergeLose = l.filter(_ != selec);
				Logger.log(mergeWin + "に合併")
				l = l.filter(_ != selec);
				mergeTile = UIAdmin.selectedTile;
				gTurn = turn
				TurnState = Aquire.turnState.MERGE 
				players(turn).removeTile(UIAdmin.selectedTile);
				return;
			}
			case Merge(x) => 
				val l = map.arroundHotels(UIAdmin.selectedTile).toArray.asInstanceOf[Array[Object]];
				mergeLose = l.filter(_ != x);
				mergeWin = x;
				gTurn = turn;
				mergeTile = UIAdmin.selectedTile;
				Logger.log(mergeWin + "に合併")
				TurnState = Aquire.turnState.MERGE 
				players(turn).removeTile(UIAdmin.selectedTile);
				return
			case CanNotPut() => return
			case _ => map.putTile(UIAdmin.selectedTile)
		}
		players(turn).removeTile(UIAdmin.selectedTile);
		UIAdmin.selectedTile = UIAdmin.nullTile ;
		UIAdmin.tileFeedback = UIAdmin.nullTile ;
		TurnState = Aquire.turnState.BUY 
	}
	def disapHotel(win:String,lose:String){
		if(players(turn).stocks(hotels(lose).name) == 0){
			UIAdmin.setPrivateMode(false);
			turn = (turn + 1) % players.size
		}else{
			TurnState = Aquire.turnState.WAIT
			val a = new MergeView(hotels(win),hotels(lose),players(turn));
			a.visible = true;
		}
	}
	def canEnd() :Boolean = {
		hotels.exists(x => x._2 .tiles >= Aquire.BigSize) ||
		(!hotels.exists(x => x._2 .tiles != 0 && x._2 .tiles < Aquire.SafeSize ) &&
		hotels.exists(x => x._2 .tiles != 0))
	}
	
	def stockHolderBonuse(hotel:Hotel) = {
		hotel.majorityBonuse(players.toList);
		hotel.minorityBonuse(players.toList);
	}
	def gameEnd() = {
		for(hn <- Aquire.Hotels){
			val hotel = hotels(hn)
			stockHolderBonuse(hotel)
			for(player <- players){
				player.money += player.stocks (hotel.name) * hotel.price;
				player.addStock(hotel.name , player.stocks (hotel.name));
			}
		}
	}
}