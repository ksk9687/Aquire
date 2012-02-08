object UIAdmin {
	var showPrivateData = false
	
	val nullTile = (-1,-1)
	var tileFeedback = nullTile
	var selectedTile =nullTile
	
	def setPrivateMode(f:Boolean) = {
		showPrivateData = f;
		tileFeedback = nullTile
		selectedTile = nullTile
	}
}