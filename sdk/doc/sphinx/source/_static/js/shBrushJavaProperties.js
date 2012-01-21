/**
 * Custom brush for SyntaxHighlighter (http://alexgorbatchev.com/)
 *
 * Use this brush to highlight Java .properties file syntax.
 */
SyntaxHighlighter.brushes.JavaProperties = function()
{
        this.regexList = [
    { regex: /[#!].*$/gm, css: 'comments' },
    { regex: /[:=].*$/gm, css: 'string' }
  ];
};

SyntaxHighlighter.brushes.JavaProperties.prototype  = new SyntaxHighlighter.Highlighter();
SyntaxHighlighter.brushes.JavaProperties.aliases    = ['java-properties'];
