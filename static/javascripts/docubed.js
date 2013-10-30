var codeMirrorArray = new Array();

function ajaxFileUpload(container)
{
    var inputContainerForm = $(container).parent().prev().parent().html();
    var divContainer = $(container).parent().prev();
    var input = $(container).parent().prev().children("input");
    $(input).upload('/uploadFile', function(res) {
        var data = $.parseJSON($(res).html());
        if(data.contentType.substring(0,5) == 'image')
            input.parent().parent().html('<label type="text" class="fileString" name="file">Insert this code to content: !['+ data.filename +']('+ data.filepath +')</label>'+inputContainerForm);
        else//[example](http://url.com/ "Title")
            input.parent().parent().html('<label type="text" class="fileString" name="file">Insert this code to content: [' + data.filename +']('+ data.filepath +')</label>'+inputContainerForm);
    }, 'json');

}


var execution = function(i){

    var codeMirror = codeMirrorArray[i];
    var textArea = $('.code-textarea')[i];
    $.ajax({
        type: "POST",
        url: "/execute",
        contentType: "text/html",
        data: codeMirror.getValue()//$(textArea).text()
    })
    .done(function(msg) {
            $($(".result-print")[i]).html(msg);
    })
    .fail(function(msg) {
            $($(".result-print")[i]).text(msg.toString());
    })
}

var deletePage = function(){

    if(confirm('정말 삭제 하시겠습니까?')){
        $.ajax({
            type: "DELETE",
            url: location.pathname
        }).done(function(msg){
            location.href = msg;
        }).fail(function(msg){
            alert("fail");
        })

    }
}


var markdownRendering = function(origin, target){
    $(target).html((marked($(origin).text()))) ;

    $('code[class^="lang-"]').each(function(i){
        $(this).parent().after('<button onclick="execution('+i+');" class="pure-button pure-button-small pure-button-secondary">Execute</button>' +
            '<div class="result pure-u-1"><div class="result-title">Result</div><xmp class="result-print pure-u-1"></xmp></div>');
    });
    $('code[class^="lang-"]').each(function(){
        $(this).replaceWith('<textarea class="code-textarea pure-u-1">'+$(this).text()+'</textarea>');
    });
}


$('#content-textarea').on('input keyup', function(){
    $(this).html($(this).val());
});
$('#content-textarea').on('input keyup', function(){
    markdownRendering('#content-textarea', '#preview');
    $('.code-textarea').each(function(i){
        var codeMirror = CodeMirror.fromTextArea(this, {
            lineNumbers: true,
            mode:  "javascript",
            theme: "solarized",
            indentUnit: 4,
            smartIndent: true
        });
        codeMirrorArray.push(codeMirror);
    });
});


markdownRendering('#content', '#content');
$(".docu-content").show();
markdownRendering('#content-textarea', '#preview');

$('#content-textarea').autosize();

$('.code-textarea').each(function(i){
    var codeMirror = CodeMirror.fromTextArea(this, {
        lineNumbers: true,
        mode:  "javascript",
        theme: "solarized",
        indentUnit: 4,
        smartIndent: true
    });
    codeMirrorArray.push(codeMirror);
});