function http_request() {
  if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
    return new XMLHttpRequest();
    }
  else
    {// code for IE6, IE5
    return new ActiveXObject("Microsoft.XMLHTTP");
  }
}
var xmlhttp = http_request();

function tokenize (txt) {
  var tokens = new Array(), oldTxt=null;
  while( txt != "" && oldTxt != txt ) {
    oldTxt = txt;
    txt = txt.replace( /^\s*(;[^\r\n]*(\r|\n|$)|#\\[^\w]|#?(\(|\[|{)|\)|\]|}|\'|`|~@|~|\"(\\(.|$)|[^\"\\])*(\"|$)|[^\s()\[\]{}]+)/,
      function($0,$1) {
        if( $1.charAt(0) != ';' ) tokens[tokens.length]=$1;
        return "";
      } );
  }
  return tokens;
}

function is_complete (command) {
  var angulars = brackets = parens = 0,
      token, tokens, _i, _len;
  tokens = tokenize (command);
  console.log(''+tokens.join(' -> '));

  for (_i = 0, _len = tokens.length; _i < _len; _i++) {
    token = tokens[_i];
    switch (token) {
      case '#{':
        ++angulars;
        break;
      case '{':
        ++angulars;
        break;
      case '}':
        --angulars;
        break;
      case '[':
        ++brackets;
        break;
      case ']':
        --brackets;
        break;
      case '(':
        ++parens;
        break;
      case ')':
        --parens;
    }
  }
  return angulars <=0 && parens <= 0 && brackets <= 0;
};
function push_command (command) {
  //var request = new XMLHttpRequest();
  xmlhttp.open('POST', '/compile', false);
  xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
  xmlhttp.send('command='+encodeURIComponent(command)); // because of "false" above, will block until the request is done

  if (xmlhttp.status === 200) {
    console.log(xmlhttp.responseText);
    return xmlhttp.responseText;
  }
}

$(function() {
  // Creating the console.
  var header = 'Welcome to Chlorine!\n';
  window.jqconsole = $('#console').jqconsole(header, 'Cl2> ');

  // Abort prompt on Ctrl+Z.
  jqconsole.RegisterShortcut('Z', function() {
    jqconsole.AbortPrompt();
    handler();
  });

  // Move to line start Ctrl+A.
  jqconsole.RegisterShortcut('A', function() {
    jqconsole.MoveToStart();
    handler();
  });

  // Move to line end Ctrl+E.
  jqconsole.RegisterShortcut('E', function() {
    jqconsole.MoveToEnd();
    handler();
  });

  jqconsole.RegisterMatching('{', '}', 'brace');
  jqconsole.RegisterMatching('(', ')', 'paran');
  jqconsole.RegisterMatching('[', ']', 'bracket');
  // Handle a command.
  var handler = function(command) {
    if (command) {
      try {
        var js_command = push_command(command);
        jqconsole.Write('==> ' + window.eval(js_command) + '\n');
      } catch (e) {
        jqconsole.Write('ERROR: ' + e.message + '\n');
      }
    }
    jqconsole.Prompt(true, handler, function (command) {
      if (is_complete(command)) {
        return false
        }
        else {
          return 1;
        }
      });
  };

  // Initiate the first prompt.
  handler();
});
