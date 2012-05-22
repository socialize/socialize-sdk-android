import sys, os
source_suffix = '.rst'
master_doc = 'index'
project = u'Socialize Android SDK'
copyright = u'2012, Socialize Inc'
version = '@socialize.version'
release = '@socialize.version'
exclude_patterns = []
pygments_style = 'vs'
highlight_language = 'java'
html_theme = 'nature'
html_theme_options = {
    "sidebarwidth": "280",
}
html_favicon = 'favicon.ico'
html_static_path = ['_static']
html_sidebars = {
   '**': ['searchbox.html', 'globaltoc.html']
}
html_domain_indices = False
html_use_index = False
html_show_sphinx = False
html_show_copyright = True
html_link_suffix = '.html?v=@socialize.version'
htmlhelp_basename = 'SocializeSDKAndroiddoc'
latex_documents = [
  ('index', 'SocializeSDKAndroid.tex', u'Socialize Android SDK Documentation',
   u'Socialize Inc', 'manual'),
]
man_pages = [
    ('index', 'socializesdkandroid', u'Socialize Android SDK Documentation',
     [u'Socialize Inc'], 1)
]
