import scala.collection.mutable.HashMap




class AquireMap() {
    val status : Array[Array[Option[String]]] = {
        val tmp = new Array[Array[Option[String]]](Aquire.MapX);
        for (i <- 0 to (Aquire.MapX - 1)) {
            tmp(i) = new Array[Option[String]](Aquire.MapY)
            for (j <- 0 to (Aquire.MapY - 1)) {
                tmp(i)(j) = None
            }
        }
        tmp
    }
    
    def changeHotel(from:String,to:String) = {
        for (i <- 0 to (Aquire.MapX - 1)) {
            for (j <- 0 to (Aquire.MapY - 1)) {
            	if(status(i)(j) == Some(from)){
            		status(i)(j) = Some(to)
            	}
            }
		}
    }
    def mergeHotels(p : (Int, Int),hotel:String) = {
    	status(p._1)(p._2) match {
			case Some(x) => 
				changeHotel(x,hotel);
				GameAdmin.hotels(hotel).merge(GameAdmin.hotels(x));
			case _ =>
		}
    }
    def arround(p:(Int,Int)):List[(Int,Int)] = {
    	var l = List[(Int,Int)]()
    	for(x <- List((0,-1),(1,0),(0,1),(-1,0))){
    		val px = p._1  + x._1;
    		val py = p._2  + x._2;
    		if (px  >= 0 && px < Aquire.MapX && py >= 0 && py  < Aquire.MapY){
    			l = (px,py):: l
    		}
    	}
    	l
    }
    
    def putTile(p : (Int, Int)):Unit = {
    	putMode(p) match{
    		case NotChain() => status(p._1)(p._2) = Some(Aquire.NotHotelName)
    		case Merge(hotel) => {
    			status(p._1)(p._2) = Some(hotel);
    			GameAdmin.hotels(hotel).tiles += 1;
		    	for(x <- arround(p)){
	    			status(x._1 )(x._2 ) match{
	    				case Some(h) if (h == Aquire.NotHotelName) => 
	    					status(x._1 )(x._2 ) = Some(hotel);
	    					GameAdmin.hotels(hotel).tiles += 1;
	    				case Some(h) if (h != hotel) => mergeHotels(x,hotel);
	    				case _ =>
	    			}
		    	}
    		}
    		case Extend(hotel) =>
    			status(p._1)(p._2) = Some(hotel)
		    	GameAdmin.hotels(hotel).tiles += 1;
		    	for(x <- arround(p)){
	    			status(x._1 )(x._2 ) match{
	    				case Some(h) if (h == Aquire.NotHotelName) => 
	    					status(x._1 )(x._2 ) = Some(hotel);
	    					GameAdmin.hotels(hotel).tiles += 1;
	    				case _ =>
	    			}
		    	}
    		case _ => throw new Exception
    	}
    }
    
    def putTile(p : (Int, Int),hotel:String):Unit = {
    	putMode(p) match{
    		case Establish(x) if x.contains(hotel) => 
    			status(p._1)(p._2) = Some(hotel)
	    		GameAdmin.hotels(hotel).tiles += 1;
		    	for(x <- arround(p)){
	    			status(x._1)(x._2) match{
	    				case Some(h) if (h == Aquire.NotHotelName) => 
	    					status(x._1)(x._2) = Some(hotel);
	    					GameAdmin.hotels(hotel).tiles += 1;
	    				case _ => 
	    			}
		    	}
    		case ChooseMerge(x) if x.contains(hotel) => 
    			status(p._1)(p._2) = Some(hotel)
	    		GameAdmin.hotels(hotel).tiles += 1;
		    	for(x <- arround(p)){
	    			status(x._1 )(x._2 ) match{
	    				case Some(h) if (h == Aquire.NotHotelName) => 
	    					status(x._1 )(x._2 ) = Some(hotel);
	    					GameAdmin.hotels(hotel).tiles += 1;
	    				case Some(h) if (h != hotel) => mergeHotels(x,hotel);
	    				case _ =>
	    			}
		    	}
    		case _ => putTile(p)
    	}
    }
    
    def canPut(p : (Int, Int)):Boolean = {
    	putMode(p) match{
    		case Establish(_) => true
    		case Merge(_) => true
    		case ChooseMerge(_) => true
    		case Extend(_) => true
    		case NotChain() => true
    		case _ => false
    	}
    }
    
    def neverPut(p : (Int, Int)):Boolean = {
    	putMode(p) match{
    		case NeverPut() => true
    		case _ => false
    	}
    }
    def canNottPut(p : (Int, Int)):Boolean = {
    	putMode(p) match{
    		case CanNotPut() => true
    		case _ => false
    	}
    }
    def arroundHotels(p : (Int, Int)):List[String]={
    	var r = List[String]()
    	for(x <- arround(p)){
    		status(x._1 )(x._2) match{
				case Some(s) if s != Aquire.NotHotelName => r = (s :: r)
				case _ => ;
			}
    	}
    	r.distinct;
    }
    def putMode(p : (Int, Int)):TilePutMode = {
		status(p._1)(p._2) match {
			case Some(_) => return NeverPut() //無理
			case _ =>
		}
    	var r = List[(String,Int)]()
    	for(x <- arround(p)){
			status(x._1 )(x._2) match{
				case Some(s) if s != Aquire.NotHotelName => r = ((s,GameAdmin.hotels(s).tiles) :: r)
				case Some(s) => r = ((s,1) :: r)
				case None => ;
			}
    	}
    	r = r.distinct
    	if(r.count(_._2  >= Aquire.SafeSize) >= 2) NeverPut()//独立ホテルに挟まれ付けられない
    	else if (r.length == 0) NotChain()//接してない
    	else if (r.count(_._1  != Aquire.NotHotelName) == 1){
    		r.find(_._1  != Aquire.NotHotelName) match{
    			case Some(x) => Extend(x._1);
    			case _ => throw new Exception;
    		}
    	}
    	else if (r.exists(_._1  != Aquire.NotHotelName )){
    		val candiate = r.foldLeft((List[String](),2)){
    			(x,y) => {
    				if (x._2 < y._2) {
    					(List(y._1) ,y._2 )
    				}else if(x._2 == y._2){
    					(y._1:: x._1 ,y._2 )
    				}else{
    					x
    				}
    			}
    		}._1 
    		if(candiate.size > 1){
    			ChooseMerge(candiate)
    		}else{
    			Merge(candiate.head)
    		}
    	}
    	else if (canEstablishAny) Establish(canEstablishList().toList)
    	else CanNotPut()
    }
    

    
    
    def canEstablish(name:String):Boolean = GameAdmin.hotels(name).tiles == 0
    def canEstablishAny():Boolean = Aquire.Hotels.exists(canEstablish _)
    def canEstablishList():Array[String] = Aquire.Hotels.filter(n=>canEstablish(n))
}

    abstract class TilePutMode;
    case class Establish(hotels:List[String]) extends TilePutMode;
    case class Merge(hotel:String) extends TilePutMode;
    case class ChooseMerge(hotels:List[String]) extends TilePutMode;
    case class Extend(hotel:String) extends TilePutMode;
    case class NotChain() extends TilePutMode;
    case class NeverPut() extends TilePutMode;
    case class CanNotPut() extends TilePutMode;