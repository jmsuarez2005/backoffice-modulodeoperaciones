<%@taglib uri="/struts-tags" prefix="s" %>

<script>
    $(document).ready(function() {
        jQuery('#infoMessage').slideDown();
    }); 
    $(document).ready(function() {
        jQuery('#successMessage').slideDown();
    }); 
    $(document).ready(function() {
        jQuery('#errorMessage').slideDown();
    }); 
</script>

<s:if test="%{message!=null && !message.equals('')}">
    <div class="infoMessage" id="infoMessage" style="display:none;">  
          <s:property value="message" />
    </div>
</s:if>

<s:if test="%{successMessage!=null && !successMessage.equals('')}">
    <div class="successMessage" id="successMessage" style="display:none;">  
          <s:property value="successMessage" />
    </div>
</s:if>

<s:if test="%{errorMessage!=null && !errorMessage.equals('')}">
    <div class="errorMessage" id="errorMessage" style="display:none;">  
          <s:property value="errorMessage" />
    </div>
</s:if>