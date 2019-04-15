<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta name="format-detection" content="telephone=no" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="format-detection" content="telephone=no" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>portalJSONP测试 -淘淘商城</title>
<!--结算页面样式-->
<link rel="stylesheet" type="text/css" href="/css/base.css" media="all" />
<link type="text/css" rel="stylesheet" href="/css/order-commons.css"
	source="widget" />
<script type="text/javascript" src="/js/jquery-1.6.4.js"></script>


<script type="text/javascript">
function fun(data){
	alert(data.id);
}
$(function(){
	 $.ajax({
		url:"http://localhost:8088/js/sso_json.json",
		type:"get",
		dataType:"script",
		success:function(data){
			alert("success");
		}
	}); 
	
	
	/*  $.ajax({
		url:"http://localhost:8088/js/sso_js.js",
		type:"get",
		dataType:"script",
		success:function(data){
			alert("jschengg");
		}
	});  */
})
</script>
</head>
<body>
</body>
</html>