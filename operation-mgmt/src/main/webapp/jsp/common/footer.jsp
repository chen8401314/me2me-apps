<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="site-footer">
    <div class="text-center">
        2014-2016 &copy; 拙心（上海）网络科技有限公司
        <a href="#" class="go-top">
            <i class="fa fa-angle-up"></i>
        </a>
    </div>
</footer>
<script>
$("select[data-value]").each(function(){
	var value= $(this).attr("data-value");
	$(this).find("option[value='"+value+"']").attr("selected","selected");
	
})

</script>