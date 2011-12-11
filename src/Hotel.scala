class Hotel(val name:String) {
	var stock = Aquire.StockNum 
	var tiles = 0
	def price:Int = {
		if(canEstablish) 0
		else	Aquire.StockPriceTable (Aquire.CalcHotelRank(name,tiles))
	}
	def merge(h:Hotel){
		tiles += h.tiles 
		h.disap()
	}
	def disap(){
		Logger.log(name + "が消滅")
		tiles = 0 
	}
	def canEstablish() :Boolean = {
		tiles == 0
	}
	def buyableStocks():Int = {
		if(tiles == 0){
			0
		}else{
			stock
		}
	}
	
	def majorityBonuse(players:List[Player]) :Unit= {
		val m = getMajority(players);
		if(m.size ==0 || tiles == 0){
			return ;
		}
		val bonuse = Aquire.normalizeMoney( Aquire.MajorityTable(Aquire.CalcHotelRank(name, tiles)) / m.size );
		
		for(p <- m){
			p.money += bonuse;
			Logger.log(p.name + " さん " + name + "の MajorityBonuse " + bonuse +" を獲得")
		}
	}
	def minorityBonuse(players:List[Player]):Unit = {
		val m = getMinority(players);
		if(m.size ==0|| tiles == 0){
			return ;
		}
		val bonuse = Aquire.normalizeMoney( Aquire.MinorityTable(Aquire.CalcHotelRank(name, tiles)) / m.size );
		
		for(p <- m){
			p.money += bonuse;
			Logger.log(p.name + " さん " + name + "の MinorityBonuse " + bonuse +" を獲得")
		}
	}
	
	
	def getMajority(players:List[Player]) :List[Player] = {
		var ans = List[Player]();
		var max = 1;
		for(p <- players){
			if(max < p.stocks(name)){
				max = p.stocks(name);
				ans = List(p)
			}else if(max == p.stocks(name)){
				ans = p :: ans
			}
		}
		return ans
	}
	def getMinority(players:List[Player]) :List[Player] = {
		var ans = List[Player]();
		var max = 1;
		var next = 1;
		for(p <- players.sort((a,b) => a.stocks(name) > b.stocks(name))){
			if(next == p.stocks(name)){
				ans = p :: ans
			}else if(next < p.stocks(name)){
				if(max <= p.stocks(name)){//はじめだけしか起きない
					max = p.stocks(name);
					next =  p.stocks(name);
					ans = p :: ans
				}else{
					next =  p.stocks(name);
					ans = List(p)
				}
			}else{
				if(max == next){
					next =  p.stocks(name);
					ans = List(p)
				}
			}
		}
		return ans
	}
}