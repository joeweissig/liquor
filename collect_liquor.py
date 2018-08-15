from bs4 import BeautifulSoup
import requests
import json

s = requests.session()

r = s.get("http://www.oregonliquorsearch.com")
r2 = s.post("http://www.oregonliquorsearch.com/servlet/WelcomeController", data={'selMonth': '1', 'selDay': '1', 'selYear': '1900', 'btnSubmit': 'Enter Site'})

getcategories = s.get("http://www.oregonliquorsearch.com/home.jsp")

soup = BeautifulSoup(getcategories.content, "html.parser")
categories = soup.select("div#browse-content")[0]
listelements = categories.select("li")
catURLs = []
for element in listelements:
    # print(element.text)
    catURLs.append("http://oregonliquorsearch.com/servlet/FrontController?view=browsecategoriesallsubcategories&action=select&category=" + element.text)

# required to "refresh" the session's cookies
blank = s.get("http://oregonliquorsearch.com/servlet/FrontController?view=browsecategoriesallsubcategories&action=select&category=RUM")

drinks = {}

for url in catURLs:
    rows = []
    r3 = s.get(url)
    print(url)
    tempsoup = BeautifulSoup(r3.content, "html5lib")
    rows.extend(tempsoup.select("tr.alt-row"))
    rows.extend(tempsoup.select("tr.row"))
    drinks[url.split("=")[-1]] = []
    for item in rows:
        if item.select("td")[2].text == "750 ML":
            drinks[url.split("=")[-1]].append({"name": item.select("td")[1].text, "size": item.select("td")[2].text, "price": item.select("td")[6].text})

f = open("alcohol.json", "w")
f.write(json.dumps(drinks))
f.close()
#f = open("categories.html", "wb")
#f.write(getcategories.content)
#f.close()

# rumResponse = s.get("http://www.oregonliquorsearch.com/servlet/FrontController?view=browsercategoriesallsubcategories&action=select&category=RUM")
