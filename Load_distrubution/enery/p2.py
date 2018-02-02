#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jan 23 16:34:16 2018

@author: prince
"""

from pandas import read_csv
from pandas import datetime
from matplotlib import pyplot
from statsmodels.tsa.arima_model import ARIMA
from sklearn.metrics import mean_squared_error
import numpy
from math import sqrt
import matplotlib.dates as mdates

print(datetime.now)
def parser(a,b,c,d):
    return datetime.strptime('%s %s %s %s' % (a, b, c, d),'%Y %j %H %M')

slot=1440

series = read_csv('sample.csv', header=0, parse_dates=[[0,1,2,3]],index_col=0, date_parser=parser)
X = series.values
#size = int(len(X) * 0.75)
size = 87840-1440
train, test = X[0:size], X[size:len(X)]
history = [x for x in train]
predictions = list()

# create a differenced series
def difference(dataset, interval=slot):
	diff = list()
	for i in range(interval, len(dataset)):
		value = dataset[i] - dataset[i - slot]
		diff.append(value)
	return numpy.array(diff)

# invert differenced value
def inverse_difference(history, yhat, interval=slot):
	return yhat + history[-interval]

#def test(inter=2):
#    print(inter)
#   

differenced=difference(X)

#model = ARIMA(differenced, order=(5,1,0))
#model_fit = model.fit(disp=0)
#forecast = model_fit.forecast(steps=1440)[0]
#    for cnt in range(0,15):


#min=1
#t=0
#for yhat in forecast:
#    inverted = inverse_difference(history, yhat)
#    print("test t=%d cnt=%d " % (t,min))
##        output = model_fit.forecast()
##        yhat=output[0]
#    predictions.append(inverted)
#    obs=test[t]
##        history.append(obs)
#    history.append(inverted)
#    print('predicted=%f, expected=%f' % (inverted, obs))
#    min+=1
#    t+=1
#    if(t==len(test)):
#        break


t=0
while t<len(test):
    model = ARIMA(differenced, order=(7,1,0))
    model_fit = model.fit(disp=0)
    forecast = model_fit.forecast(steps=slot)[0]
#    for cnt in range(0,15):
    min=1
    for yhat in forecast:
        inverted = inverse_difference(history, yhat)
        print("test t=%d cnt=%d " % (t,min))
#        output = model_fit.forecast()
#        yhat=output[0]
        predictions.append(inverted)
        obs=test[t]
#        history.append(obs)
        history.append(obs)
        print('predicted=%f, expected=%f' % (inverted, obs))
        min+=1
        t+=1
        if(t==len(test)):
            break

error = mean_squared_error(test, predictions)
mse = sqrt(error)
print('Test MSE: %.3f, RMSE: %.3f' % (error,mse))

# plot
xfmt = mdates.DateFormatter('%d-%m-%y %H:%M')
fig, ax = pyplot.subplots(1)

pyplot.plot(series.index.values[size:len(X)],test)
pyplot.plot(series.index.values[size:len(X)],predictions, color='red')
fig.autofmt_xdate()
pyplot.xlabel('Date & Time',fontsize=20)
pyplot.ylabel('Irradiance (W/m^2)',fontsize=20)
ax.xaxis.set_major_formatter(xfmt)
pyplot.show()
###################################################3Z

"""
for t in range(len(test)):
    model = ARIMA(differenced, order=(5,1,0))
    model_fit = model.fit(disp=0)
#    output = model_fit.forecast()
    forecast = model_fit.forecast(steps=10)[0]
    min = 1
    for yhat in forecast:
        inverted = inverse_difference(history, yhat)
        print('Min %d: %f' % (min, inverted))
        history.append(inverted)
        min += 1
#    yhat = output[0]
    predictions.append(inverted)
    obs = test[t]
#    history.append(obs)
#    print(t,end=',')
    print('predicted=%f, expected=%f' % (yhat, obs))
#print()
error = mean_squared_error(test, predictions)
print('Test MSE: %.3f' % error)
# plot
pyplot.plot(test)
pyplot.plot(predictions, color='red')
pyplot.show()


differenced=difference(X)

# fit model
model = ARIMA(differenced, order=(5,1,0))
model_fit = model.fit(disp=0)
# print summary of fit model
#print(model_fit.summary())


forecast = model_fit.forecast(steps=10)[0]

min = 1
for yhat in forecast:
	inverted = inverse_difference(history, yhat)
	print('Min %d: %f' % (min, inverted))
	history.append(inverted)
	min += 1
    
"""
