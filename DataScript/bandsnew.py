import xml.etree.ElementTree as ET
import requests
import musicbrainzngs
import json

ID = 0

api_key = "a8957e6fbc07c4b79dec968535bf1a4a"

bands = {}

#GET TOP ARTISTS
for i in range(2,5):
	artistslist = requests.get('http://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&page='+str(i)+'&api_key='+api_key)
	tree = ET.fromstring(artistslist.content)
	for child in tree:
		for artist in child.findall('artist'):
			name = artist.find('name').text
			url = artist.find('url').text
			mbid = artist.find('mbid').text
			bands[ID] = {}
			bands[ID]['ID'] = ID
			bands[ID]['Name'] = name
			bands[ID]['URL'] = url
			if (mbid is not None):
				bands[ID]['MBID'] = mbid
			else:
				bands[ID]['MBID'] = "None"
			ID+=1

musicbrainzngs.set_useragent("app", "version", contact=None)

#GET ARTIST INFO
for i,v in bands.items():
	if bands[i]['MBID'] != "None":
		info = musicbrainzngs.get_artist_by_id(bands[i]['MBID'])
		information = dict(list(info.values())[0])
		if 'type' in information:
			tipo = information['type']
			bands[i]['Type'] = tipo
			if tipo == "Person":
				if 'gender' in information:
					bands[i]['gender'] = information['gender']
				else:
					bands[i]['gender'] = "None"
			else:
				bands[i]['gender'] = "None"
		else:
			bands[i]['Type'] = "None"
		if 'life-span' in information:
			dates = information['life-span']
			if (len(dates.values()) > 1):
				if (dates['ended'] == 'true'):
					try:
						bands[i]['EndDate'] = dates['end']
					except:
						bands[i]['EndDate'] = "None"
				else:
					bands[i]['EndDate'] = "None"
				bands[i]['BeginDate'] = dates['begin']
			elif (len(dates.values()) == 1):
				bands[i]['EndDate'] = "None"
				bands[i]['BeginDate'] = dates['begin']
		else:
			bands[i]['EndDate'] = "None"
			bands[i]['BeginDate'] = "None"
		if 'area' in information:
			bands[i]['Location'] = information['area']['name']
		else:
			bands[i]['Location'] = "None"
	else:
		bands[i]['Type'] = "None"
		bands[i]['EndDate'] = "None"
		bands[i]['BeginDate'] = "None"
		bands[i]['Location'] = "None"
	chosen = bands[i]['Name'].replace(" ", "+")
	artist = requests.get('http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist='+chosen+'&api_key='+api_key)
	spotify_artist = requests.get('https://api.spotify.com/v1/search?q='+chosen+"&type=artist").json()
	bands[i]['Genres'] = spotify_artist['artists']['items'][0]['genres']
	tree = ET.fromstring(artist.content)
	for child in tree:
		for artist in child:
			if (artist.get('size') == "large"):
				if (artist.text is not None):
					bands[i]['Image'] = artist.text
			for bio in artist.findall('summary'):
				if (bio.text is not None):
					bands[i]['Description'] = bio.text
				else:
					bands[i]['Description'] = bio.text
	print(bands[i]['Name'] + " INFO RETRIEVED")

with open('../data/artists.json', 'w') as outfile:
    json.dump(bands, outfile)

with open('../data/artists.json') as data_file:    
    bands = json.load(data_file)

data_file.close()

albuns = {}

#GET TOP ALBUNS FROM ARTISTS
for i,v in bands.items():
	chosen = bands[i]['Name'].replace(" ", "+")
	topalbuns = requests.get('http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist='+chosen+'&api_key='+api_key)
	tree = ET.fromstring(topalbuns.content)
	for child in tree:
		for album in child:
			name = album.find('name').text
			url = album.find('url').text
			albuns[ID] = {}
			albuns[ID]['ID'] = ID
			albuns[ID]['Artist'] = bands[i]['Name']
			albuns[ID]['ArtistID'] = bands[i]['ID']
			albuns[ID]['Title'] = name
			albuns[ID]['URL'] = url
			if (album.find('mbid') is not None):
				mbid = album.find('mbid').text
				albuns[ID]['MBID'] = mbid
			else:
				albuns[ID]['MBID'] = "None"
			ID+=1
	print(bands[i]['Name'] + " ALBUNS RETRIEVED")

#GET ALBUM INFO
for i,v in albuns.items():
	albuns[i]['Image'] = "None"
	albuns[i]['Description'] = "None"
	if albuns[i]['MBID'] != "None":
		try:
			info = musicbrainzngs.get_release_by_id(albuns[i][3])
			information = dict(list(info.values())[0])
			if 'date' in information:
				dates = information['date']
				albuns[i]['Date'] = dates
			else:
				albuns[i]['Date'] = "None"
		except:
			albuns[i]['Date'] = "None"
	else:
		albuns[i]['Date'] = "None"
	artist = albuns[i]['Artist'].replace(" ","+")
	title = albuns[i]['Title'].replace(" ", "+")
	album = requests.get('http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key='+api_key+'&artist='+artist+'&album='+title)
	spotify_album_search = requests.get('https://api.spotify.com/v1/search?q=album:'+title+'%20artist:'+artist+'&type=album').json()
	try:
		spotify_id = spotify_album_search['albums']['items'][0]['id']
		spotify_album = requests.get('https://api.spotify.com/v1/albums/'+spotify_id).json()
		albuns[i]['Date'] = spotify_album['release_date']
	except:
		albuns[i]['Genres'] = []
	tree = ET.fromstring(album.content)
	for child in tree:
		for artist in child:
			if (artist.get('size') == "large"):
				if (artist.text is not None):
					albuns[i]['Image'] = artist.text
			for wiki in artist.findall('summary'):
				if (wiki.text is not None):
					albuns[i]['Description'] = wiki.text
				else:
					albuns[i]['Description'] = "None"
	print("Album "+albuns[i]['Title']+" by "+albuns[i]['Artist']+ " RETRIEVED")

with open('../data/albuns.json', 'w') as outfile:
    json.dump(albuns, outfile)


with open('../data/albuns.json') as data_file:    
    albuns = json.load(data_file)

data_file.close()

tracks = {}
#GET TRACKS FOR ALL THE ALBUMS
for i,v in albuns.items():
	artist = albuns[i]['Artist'].replace(" ","+")
	title = albuns[i]['Title'].replace(" ", "+")
	album = requests.get('http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key='+api_key+'&artist='+artist+'&album='+title)
	tree = ET.fromstring(album.content)
	for child in tree:
		for artist in child.findall('tracks'):
			for track in artist.findall('track'):
				rank = track.get('rank')
				name = track.find('name').text
				length = track.find('duration').text
				tracks[ID] = {}
				tracks[ID]['Title'] = name
				tracks[ID]['Number'] = rank
				tracks[ID]['Artist'] = albuns[i]['Artist']
				tracks[ID]['Album'] = albuns[i]['Title']
				tracks[ID]['AlbumID'] = albuns[i]['ID']
				tracks[ID]['Length'] = length
				ID+=1
	print ("Album "+albuns[i]['Title'] + " by " + albuns[i]['Artist'] + " COMPLETED")

with open('../data/tracks.json', 'w') as outfile:
    json.dump(tracks, outfile)

with open('../data/tracks.json') as data_file:    
    tracks = json.load(data_file)

print(len(tracks))

