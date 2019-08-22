 $(function(){ 
	    //var webUrl="http://10.33.4.222:8080";
	    //var serUrl="http://localhost:8080";
	    var serUrl = "http://"+window.location.host;
	    var webUrl = "http://"+window.location.host;
	    var token = getCookieValue("token");
	    if(token!=""){
	    	window.location.href = webUrl+"/html/cubeBuildManager.html";
	    }
		$("#xiao-submit-button").on("click", function () {
			var username = $("#username").val();
			var password = $("#userPassword").val();
			if (username == '') {
				alert("用户名不能为空！");
				return false;
			}
			if (password == '') {
				alert("密码不能为空！");
				return false;
			}
			$.ajax({
				type : "POST",
				url : "/login",
				data : {'username': username, 'password': password},
				dataType : 'json',
				success : function(data, textStatus, jqXHR) {
					//alert(data.status);
					if(data.status=='0'){
						window.location.href = webUrl+"/html/cubeBuildManager.html";
					}else{
						alert(data.msg+"sdfadf");
						window.location.href = webUrl+"/html/loginPage.html";
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert("error"+jqXHR+textStatus+errorThrown);
				}
		    }); 
		});	
		
		//获取cookie值
		function getCookieValue(key) {
	    	var value = document.cookie.match('(^|;)\\s*' + key + '\\s*=\\s*([^;]+)');
	    	return value ? value.pop() : '';
	    }
	});