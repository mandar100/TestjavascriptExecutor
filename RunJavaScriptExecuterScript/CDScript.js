
function addEventHandlers(){
	var allcdDiv = document.getElementById('allcd');
	for(var i=0;i<allcdDiv.childNodes.length;i++){
		allcdDiv.childNodes[i].addEventListener('click',addToTop5,false);
		//allcdDiv.childNodes[i].onclick=addToTop5;
	}
}

function addToTop5(event){
	var currentImg = event.target;
	var allcdDiv = document.getElementById('allcd');
	var top5Div = document.getElementById('top5cd');
	var CDNum=0;
	for(var i=0;i<top5Div.childNodes.length;i++){
		if(top5Div.childNodes[i].nodeName.toLowerCase()=='img')
			CDNum = CDNum+1;
	}
	if(currentImg.nodeName.toLowerCase()=='img'){
		if(CDNum<5){
			currentImg.removeEventListener('click',addToTop5);
			//currentImg.onclick=null;
			top5Div.appendChild(currentImg);
		}
		else
			alert('You already have 5 CDs in your list')
	}
}

function startOver(){
	var allcdDiv = document.getElementById('allcd');
	var top5Div = document.getElementById('top5cd');
		
	while(top5Div.hasChildNodes()){
		var image = top5Div.firstChild;
		if(image.nodeName.toLowerCase()=='img'){
			allcdDiv.appendChild(image);
		}
	}
	addEventHandlers();
	
}