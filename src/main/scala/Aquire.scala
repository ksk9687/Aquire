import java.awt.Color


object Aquire {
	val Name = "AQUIRE"
	
	val MapX = 12
	val MapY = 9
	
	val InitMoney = 6000
	val StockNum = 25
	val HotelNum = 7
	val SafeSize = 11
	val BigSize = 41
	val HandTileNum = 6
	val InitTiles = 6;
	val Hotels = Array("Tower","CONTINENTAL","FESTIVAL","AMERICAN","IMPERIAL","Sackson","Worldwide")
	val NotHotelName= "Rocky"
	val HotelColor = Map(
			Hotels(0) -> Color.gray,
			Hotels(1) -> Color.red,
			Hotels(2) -> Color.green,
			Hotels(3) -> Color.blue,
			Hotels(4) -> Color.yellow.brighter,
			Hotels(5) -> Color.orange.darker,
			Hotels(6) -> Color.cyan,
			NotHotelName -> Color.lightGray
			)
	val EmptyColor = Color.darkGray.darker
		
	val HotelRanks = Map(
			Hotels(0) -> 2,
			Hotels(1) -> 2,
			Hotels(2) -> 1,
			Hotels(3) -> 1,
			Hotels(4) -> 1,
			Hotels(5) -> 0,
			Hotels(6) -> 0 
		)

		
	val StockPriceTable = Array(
		200,300,400,500,600,700,800,900,1000,1100,1200
	)
	val MajorityTable = Array(
		2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000
	)
	val MinorityTable = Array(
		1000,1500,2000,2500,3000,3500,4000,4500,5000,5500,6000
	)
	def Size2Rank(size:Int):Int = {
		size match{
			case x if(x < 2) => 0
			case 2 => 0
			case 3 => 1
			case 4 => 2
			case 5 => 3
			case x if(x <= 10) => 4
			case x if(x <= 20) => 5
			case x if(x <= 30) => 6
			case x if(x <= 40) => 7
			case _ => 8
		}
	}
	
	def normalizeMoney(money:Int):Int = {
		if(money % 100 > 0){
			money - money % 100 + 100;
		}else{
			money
		}
	}
	def CalcHotelRank(name:String,size:Int):Int = HotelRanks(name) + Size2Rank(size)
	
	object state extends Enumeration{
		val SETUP,INIT,PLAYER,END = Value
	}
	object turnState extends Enumeration{
		val BEGIN,TILE,MERGE,BUY,WAIT,END = Value
	}
	def getTileName(x:Int,y:Int):String = {
		val s = "ＡＢＣＤεＦＧＨＩ"
		(x+1).toString + s.charAt(y)
	}
	
	def getTileName(x:(Int,Int)):String = {
		getTileName(x._1 ,x._2 )
	}
	
	
	
	
}