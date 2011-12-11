import scala.collection.mutable.ArrayBuffer

class Player(val name:String,var money:Int) extends boardGame.Player(name){
	def this(name:String) = this(name,Aquire.InitMoney )
	
	var stocks:Map[String,Int] = {
		var m = Map[String,Int]()
		for(hotel <- Aquire.Hotels){
			m = m + (hotel -> 0)
		}
		m
	}
	var tiles = new ArrayBuffer[(Int,Int)]()
	
	def addStock(hotel:String,num:Int){
		stocks = stocks.updated(hotel ,stocks(hotel)+ num)
		GameAdmin.hotels (hotel).stock  -= num;
	}
	
	def refleshTiles(){
		if(tiles.exists(x => GameAdmin.map.neverPut(x))){
			tiles = tiles.filter(x => !GameAdmin.map.neverPut(x));
			fillTiles();
			refleshTiles();
		}
	}
	
	def removeTile(tile:(Int,Int)){
		tiles -= tile;
	}
	def fillTiles() = {
		while(tiles.size < Aquire.HandTileNum && GameAdmin.cardPool.canDraw){
			drawOneTile
		}
	}
	def drawOneTile() = {
		val tile = GameAdmin.cardPool.draw
		tiles += tile
		tiles = tiles.sortWith((x , y) => x._1 < y._1  || (x._1 == y._1 && x._2 <= y._2))
	}
}