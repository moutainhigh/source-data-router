$(function() {
	    //var webUrl="http://10.33.4.222:8080";
        //var serUrl="http://localhost:8080";
        var serUrl = "http://"+window.location.host;
        var webUrl = "http://"+window.location.host;
		var token = getCookieValue("token");
	    if(token==""){
	    	window.location.href = webUrl+"/html/loginPage.html";
	    }
	    //获取cookie值
		function getCookieValue(key) {
	    	var value = document.cookie.match('(^|;)\\s*' + key + '\\s*=\\s*([^;]+)');
	    	return value ? value.pop() : '';
	    }
		var page = 1;
		var size = 15;
		$(document).ready(function() {
			taskList();
		});
		
		//首页
		$("#first-page").on("click", function() {
			$("#first-page").addClass("current");			
			if(page==1){
				return;
			}
			$("#pre-page").removeClass();
			$("#pre-page").addClass("disabled");
			$("#next-page").removeClass();
			page = 1;
			//$(".task-tbody").reset();
			$(".task-tbody  tr:not(:first)").empty("");
			taskList();
		});
		
		//上一页
		$("#pre-page").on("click", function() {
			var prePageClaAtt=$("#pre-page").attr('class');
			if(prePageClaAtt=='disabled'){
				return;
			}
			$("#pre-page").addClass("current");
			$("#first-page").removeClass();
			$("#next-page").removeClass();
			page = page-1;
			if(page==1){
				$("#pre-page").removeClass();
				$("#pre-page").addClass("disabled");
			}
			$(".task-tbody  tr:not(:first)").empty("");
			taskList();
		});
		
		//下一页
		$("#next-page").on("click", function() {
			var nextPageClaAtt=$("#next-page").attr('class');
			if(nextPageClaAtt=='disabled'){
				return;
			}
			$("#next-page").addClass("current");
			$("#first-page").removeClass();
			$("#pre-page").removeClass();
			page = page+1;
			$(".task-tbody  tr:not(:first)").empty("");
			taskList();
		});
		
		function taskList() {		
			$.ajax({
				type : "GET",
				url : "/cube-build/getTasks",
				data : {
					'page' : page,
					'size' : size
				},
				dataType : 'json',
				success : function(data, textStatus, jqXHR) {
					for (var i = 0; i < data.length; i++) {
						var id = data[i].id;
						var taskName = data[i].taskName;
						var cubeUrl = data[i].cubeUrl;
						var triggerCron = data[i].triggerCron;
						var tr = '<tr id="task-"'+id+'>';
						tr += '<td>' + taskName + '</td>';
						tr += '<td>' + cubeUrl + '</td>';
						tr += '<td>' + triggerCron + '</td>';
						tr += '<td class="task-edit">编辑</td>';
						tr += '<td class="task-delete">删除</td>';
						tr += '</tr>';
						var $tr = $(tr);
						$(".task-tbody").append($tr);
						$("#tr_example").hide();
					}
					if(data.length<size){
						$("#next-page").removeClass();
						$("#next-page").addClass("disabled");
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert(jqXHR.status);
					toLogin(jqXHR.status);
				}
			});
		}

		//新建或者更新
		$("#submit-button").on("click", function() {
			var taskName = $("#taskName").val();
			var cubeUrl = $("#cubeUrl").val();
			var triggerCron = $("#triggerCron").val();
			if (taskName == '') {
				alert("任务名称不能为空！");
				//$("#taskName").css("border-color", "red");
				return false;
			}
			if (cubeUrl == '') { 
				alert("cubeUrl不能为空！");
				return false;
			}
			if (triggerCron == '') {
				alert("任务定时器不能为空！");
				return false;
			}
			$.ajax({
				type : "POST",
				url : "/cube-build/task",
				data : {
					'cubeUrl' : cubeUrl,
					'taskName' : taskName,
					'triggerCron' : triggerCron
				},
				dataType : 'text',
				success : function(data, textStatus, jqXHR) {
					location.reload();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert(jqXHR.status);
					toLogin(jqXHR.status);
				}
			});
		});

		//编辑
		$("#task-tbody").on("click", ".task-edit", function() {
			var taskName_te = $(this).parent().children("td").eq(0).text();
			var cubeUrl_te = $(this).parent().children("td").eq(1).text();
			var triggerCron_te = $(this).parent().children("td").eq(2).text();
			$("#taskName").val(taskName_te);
			$("#cubeUrl").val(cubeUrl_te);
			$("#triggerCron").val(triggerCron_te);
		});

		//删除
		$("#task-tbody").on("click", ".task-delete", function() {
			var taskName_td = $(this).parent().children("td").eq(0).text();
			$.ajax({
				type : "POST",
				url : "/cube-build/task-remove",
				data : {
					'taskName' : taskName_td
				},
				dataType : 'text',
				success : function(data, textStatus, jqXHR) {
					alert(data);
					location.reload();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert(jqXHR.status);
					toLogin(jqXHR.status);
				}
			});
		});
		
		function toLogin(status){
			if(status==403){
				window.location.href = webUrl+"/html/loginPage.html";
			}
		}
	});