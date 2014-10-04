$(document).ready(function() {
	// Valitaa sivu
	var sivu = location.hash.substr(1);
	sivu = (sivu == "") ? "frontpage" : sivu;
	
	$("#nav-"+sivu).addClass("active");
	$("#pagetitle").html($("#nav-"+sivu).children('a').html());
	
	// Valikko
	$("#nav a, a[href='#portfolio']").live("click", function(event) {
		event.preventDefault();
		
		$("#nav li").removeClass("active");
		$("#nav-" + $(this).attr("href").substr(1)).addClass("active");
		$("#pagetitle").html($(this).html());
	});
	
	// Nav scroll
	settings = {target: '#content', axis:   'xy', queue:  false, hash:   true}
	$.localScroll.hash(settings);
	$.localScroll(settings);
	
	// Portfolio kuva hover
	$("#sample-list li").hover(function() {
		// Hover efekti
		$("#sample-list li").removeClass("active");
		$(this).addClass("active");
		// Näytä oikea kuva
		var site = this.id.split("-")[1];
		$("#sample-images li").removeClass("active");
		$("#sample-image-" + site).addClass("active");
	});

	/* LIGHTBOX */
	lightbox = {
		current: "Kuva - {current}/{total}",
		previous: "Edellinen",
		next: "Seuraava",
		close: "Sulje",
		transition: "fade",
		photo: true,
		scalePhotos: true,
		maxWidth: "90%",
		maxHeight: "70%"};
	$("a.sample-image").colorbox(lightbox);
	$("a.img-kaappi").colorbox(lightbox);
	$("a.img-valot").colorbox(lightbox);
	$("a.img-huone").colorbox(lightbox);
	$("a.img-render").colorbox(lightbox);
	$("a.img-tekken").colorbox(lightbox);
	$("a.img-poyta").colorbox(lightbox);
	$("a.img-seka").colorbox(lightbox);
});
