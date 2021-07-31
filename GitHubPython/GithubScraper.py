from os import write
from bs4 import BeautifulSoup
import requests
import csv

link_list = []
to_write = {}

with open("C:\workspace\GitHubPython\html_links.txt", 'r') as file:
    link_list = file.readlines()

for link in link_list:
    response = requests.get(link.rstrip())
    soup = BeautifulSoup(response.text, "html.parser")
    soup.encode('utf-8')
    keys = ["users forked this repository"]
    elementList = soup.find_all('a')
    for element in elementList:
        strElement = str(element)
        if (strElement.find(keys[0]) != -1):
            numForks = ''.join(element.findAll(text=True))
            to_write[link.strip()] = numForks.strip()
            print(link)

    

with open("scrapper_result.txt", 'w', encoding='utf-8') as file:
    for key in to_write.keys():
        file.write(to_write[key] + "\n")

with open("testing.csv", 'w', encoding='utf-8') as file:
    writer = csv.writer(file)
    for key in to_write.keys():
        row = [key, to_write[key]]
        writer.writerow(row)        
