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


function execution(i){
    if ($('#preview').length > 0)
        var code = $($('.code-textarea')[i]).text();
    else
        var code = codeMirrorArray[i].getValue();


    $.ajax({
        type: "POST",
        url: "/execute",
        contentType: "text/html",
        data: code
    })
        .done(function(msg) {
            $($(".result-print")[i]).html(msg);
        })
        .fail(function(msg) {
            $($(".result-print")[i]).text(msg.toString());
        })
}

function deletePage(){

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

function makeExecutionBox(){
    $('.code-textarea').each(function(i){
        var codeMirror = CodeMirror.fromTextArea(this, {
            lineNumbers: true,
            mode:  "javascript",
            theme: "solarized",
            indentUnit: 2,
            smartIndent: true
        });
        codeMirrorArray.push(codeMirror);
    });
}



function markdownRendering(origin, target){
    //Markdown rendering
    $(target).html((marked($(origin).text()))) ;

    //Make testbed box
    $('code[class^="lang-"]').each(function(i){
        $(this).parent().after('<button onclick="execution('+i+');" class="pure-button pure-button-small pure-button-secondary">Execute</button>' +
            '<div class="result pure-u-1"><div class="result-title">Result</div><xmp class="result-print pure-u-1"></xmp></div>');
        $(this).replaceWith('<textarea class="code-textarea pure-u-1">'+$(this).text()+'</textarea>');
    });
}


//refresh preview, textarea value with typing
$('#content-textarea').on('input keyup', function(){

    //refresh textarea value
    $(this).html($(this).val());
    //refresh preview
    markdownRendering('#content-textarea', '#preview');
    //refresh codemirror value
    makeExecutionBox();
});

//init
markdownRendering('#content', '#content');
markdownRendering('#content-textarea', '#preview');
$(".docu-content").show();
makeExecutionBox();