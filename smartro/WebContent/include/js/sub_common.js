		$(window).ready(function() {
			var h = $(document).height();
			var w = $(document).width();

			$("#gridbox").css('height' ,  h-64);
			$("#gridbox").css('width' ,  w-54);

			//mygrid.setSize();
		});

		$(window).resize(function() {
			var h = $(document).height();
			var w = $(document).width();

			$("#gridbox").css('height' ,  h-64);
			$("#gridbox").css('width' ,  w-54);

			//mygrid.setSize();
		});