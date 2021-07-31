
from os import read
from github import Github
import urllib.request, json, sys


#Functions
def get_repositories_links(links):
    toReturn = []
    count = 0

    for link in links:
        req = urllib.request.Request(link.rstrip())
        req.add_header('Authorization', 'token __INSERT_GITHUB_TOKEN HERE__')
        try:
            with urllib.request.urlopen(req) as url:
                data = json.loads(url.read().decode())
                toReturn.append(data['html_url'])
                print(count)
                count = count + 1
        except:
            with open("C:\workspace\GitHubPython\\api_links_missed.txt", 'w') as file:
                file.write(link + '\n')
    return toReturn

url_list = []
with open("C:\workspace\GitHubPython\\api_links.txt", 'r') as url_file:
    url_list = get_repositories_links(url_file)

with open("C:\workspace\GitHubPython\html_links.txt", 'w') as file:
    for url in url_list:
        file.write(url + '\n')

sys.exit()
