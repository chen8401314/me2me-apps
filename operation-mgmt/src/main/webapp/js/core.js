(function() {
	window.NEJ = window.NEJ || {};
	NEJ.O = {};
	NEJ.R = [];
	NEJ.F = function() {
		return !1
	};
	NEJ.P = function(mQ) {
		if (!mQ || !mQ.length) return null;
		var tW = window;
		for (var a = mQ.split("."), l = a.length, i = a[0] == "window" ? 1 : 0; i < l; tW = tW[a[i]] = tW[a[i]] || {}, i++);
		return tW
	};
	NEJ.Q = function(kM, mQ) {
		kM = kM || NEJ.O;
		var bP = mQ.split(".");
		for (var i = 0, l = bP.length; i < l; i++) {
			kM = kM[bP[i]];
			if (!kM) break
		}
		return kM
	};
	NEJ.C = function() {
		var Go = function() {
			return NEJ.O.toString.call(arguments[0]) != "[object Function]"
		};
		var BV = function(bA, bQ) {
			for (var x in bQ)
				if (bA == bQ[x]) return x;
			return null
		};
		var zY = {
				cI: 0,
				cP: 1,
				dx: 2,
				fq: 3,
				fk: 4,
				jR: 5,
				mW: 6,
				iX: 7
			},
			jf = {
				dv: 0,
				cS: 1,
				dA: 2,
				gD: 3,
				jU: 4,
				mY: 5,
				yl: 6,
				ln: 7
			};
		return function() {
			var dh = function() {
				this.BW();
				return this.cI.apply(this, arguments)
			};
			dh.prototype.BW = NEJ.F;
			dh.prototype.cI = NEJ.F;
			dh.cg = function(ll, BX) {
				if (Go(ll)) return;
				if (BX == null || !!BX) NEJ.X(this, ll, Go);
				this.Xj = ll;
				this.ff = ll.prototype;
				var cl = function() {};
				cl.prototype = ll.prototype;
				this.prototype = new cl;
				var lQ = this.prototype;
				lQ.constructor = this;
				var dN;
				for (var x in zY) {
					dN = BV(zY[x], jf);
					if (!dN || !this.ff[x]) continue;
					lQ[x] = function(bB) {
						return function() {
							this[bB].apply(this, arguments)
						}
					}(dN)
				}
				var Cn = {};
				for (var x in jf) {
					dN = BV(jf[x], zY);
					if (!dN || !this.ff[dN]) continue;
					Cn[dN] = ll;
					lQ[x] = function(bB) {
						return function() {
							var bw, cl = this.vt[bB],
								qM = cl.prototype[bB];
							this.vt[bB] = cl.Xj || ll;
							if (!!qM) bw = qM.apply(this, arguments);
							this.vt[bB] = ll;
							return bw
						}
					}(dN)
				}
				lQ.BW = function() {
					this.vt = NEJ.X({}, Cn)
				};
				lQ.baw = lQ.dv;
				return lQ
			};
			return dh
		}
	}();
	NEJ.X = function(ds, ci, ek) {
		if (!ds || !ci) return ds;
		ek = ek || NEJ.F;
		for (var x in ci) {
			if (ci.hasOwnProperty(x) && !ek(ci[x], x)) ds[x] = ci[x]
		}
		return ds
	};
	NEJ.EX = function(ds, ci) {
		if (!ds || !ci) return ds;
		for (var x in ds) {
			if (ds.hasOwnProperty(x) && ci[x] != null) ds[x] = ci[x]
		}
		return ds
	};
	var bbM = Function.prototype;
	bbM.fR = function(wo, wP) {
		var f = NEJ.F,
			wP = wP || f,
			wo = wo || f,
			cL = this;
		return function() {
			var bi = {
				args: NEJ.R.slice.call(arguments, 0)
			};
			wo(bi);
			if (!bi.stopped) {
				bi.value = cL.apply(this, bi.args);
				wP(bi)
			}
			return bi.value
		}
	};
	bbM.bq = function() {
		var bO = arguments,
			ds = arguments[0],
			Bk = this;
		return function() {
			var jG = NEJ.R.slice.call(bO, 1);
			NEJ.R.push.apply(jG, arguments);
			return Bk.apply(ds || window, jG)
		}
	};
	bbM.jh = function() {
		var bO = arguments,
			ds = NEJ.R.shift.call(bO),
			Bk = this;
		return function() {
			NEJ.R.push.apply(arguments, bO);
			return Bk.apply(ds || window, arguments)
		}
	};
	var bbM = String.prototype;
	if (!bbM.trim) {
		bbM.trim = function() {
			var cA = /(?:^\s+)|(?:\s+$)/g;
			return function() {
				return this.replace(cA, "")
			}
		}()
	}
	if (!window.MWF) window.MWF = NEJ;
	if (!window.mwf) window.mwf = NEJ.P("nej");
	if (!window.console) {
		NEJ.P("console").log = NEJ.F;
		NEJ.P("console").error = NEJ.F
	}
	var lt, gt, amp, nbsp, quot, apos, copy, reg
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bs = bh("nej.p"),
		jZ = window.navigator.platform,
		iP = window.navigator.userAgent;
	var fm = {
		mac: jZ,
		win: jZ,
		linux: jZ,
		ipad: iP,
		ipod: iP,
		iphone: jZ,
		android: iP
	};
	bs.tm = fm;
	for (var x in fm) fm[x] = (new RegExp(x, "i")).test(fm[x]);
	fm.ios = fm.ipad || fm.iphone || fm.ipod;
	fm.tablet = fm.ipad;
	fm.desktop = fm.mac || fm.win || fm.linux && !fm.android;
	var dV = {
		engine: "unknow",
		release: "unknow",
		browser: "unknow",
		version: "unknow",
		prefix: {
			css: "",
			pro: "",
			clz: ""
		}
	};
	bs.eV = dV;
	if (/msie\s+(.*?);/i.test(iP) || /trident\/.+rv:([\d\.]+)/i.test(iP)) {
		dV.engine = "trident";
		dV.browser = "ie";
		dV.version = RegExp.$1;
		dV.prefix = {
			css: "ms",
			pro: "ms",
			clz: "MS",
			evt: "MS"
		};
		var hq = {
			6: "2.0",
			7: "3.0",
			8: "4.0",
			9: "5.0",
			10: "6.0",
			11: "7.0"
		};
		dV.release = hq[document.documentMode] || hq[parseInt(dV.version)]
	} else if (/webkit\/?([\d.]+?)(?=\s|$)/i.test(iP)) {
		dV.engine = "webkit";
		dV.release = RegExp.$1 || "";
		dV.prefix = {
			css: "webkit",
			pro: "webkit",
			clz: "WebKit"
		}
	} else if (/rv\:(.*?)\)\s+gecko\//i.test(iP)) {
		dV.engine = "gecko";
		dV.release = RegExp.$1 || "";
		dV.browser = "firefox";
		dV.prefix = {
			css: "Moz",
			pro: "moz",
			clz: "Moz"
		};
		if (/firefox\/(.*?)(?=\s|$)/i.test(iP)) dV.version = RegExp.$1 || ""
	} else if (/presto\/(.*?)\s/i.test(iP)) {
		dV.engine = "presto";
		dV.release = RegExp.$1 || "";
		dV.browser = "opera";
		dV.prefix = {
			css: "O",
			pro: "o",
			clz: "O"
		};
		if (/version\/(.*?)(?=\s|$)/i.test(iP)) dV.version = RegExp.$1 || ""
	}
	if (dV.browser == "unknow") {
		var hq = ["chrome", "maxthon", "safari"];
		for (var i = 0, l = hq.length, bB; i < l; i++) {
			bB = hq[i] == "safari" ? "version" : hq[i];
			if ((new RegExp(bB + "/(.*?)(?=\\s|$)", "i")).test(iP)) {
				dV.browser = hq[i];
				dV.version = RegExp.$1.trim();
				break
			}
		}
	}
	bs.PU = {};
	var sW = dV.engine != "trident";
	bs.xS = {
		gecko: dV.engine != "gecko",
		webkit: dV.engine != "webkit",
		presto: dV.engine != "presto",
		trident0: sW || dV.release > "2.0",
		trident1: sW || dV.release < "6.0",
		trident2: sW || dV.release > "3.0",
		trident: sW || dV.release >= "6.0"
	}
})();
(function() {
	var gn = NEJ.P("nej.c"),
		bF = {};
	var zW = function() {
		var cA = /^([\w]+?:\/\/.*?(?=\/|$))/i;
		return function(bC) {
			bC = bC || "";
			if (cA.test(bC)) return RegExp.$1;
			return location.protocol + "//" + location.host
		}
	}();
	var AJ = function() {
		var Cs = function(bn, bQ) {
			if (!bn || !bn.length) return;
			for (var i = 0, l = bn.length, kI; i < l; i++) {
				kI = bn[i];
				if (kI.indexOf("://") > 0) bQ[zW(kI)] = kI
			}
		};
		var bu = {
			portrait: {
				name: "portrait",
				dft: "portrait/"
			},
			"ajax.swf": {
				name: "ajax",
				dft: "nej_proxy_flash.swf"
			},
			"chart.swf": {
				name: "chart",
				dft: "nej_flex_chart.swf"
			},
			"audio.swf": {
				name: "audio",
				dft: "nej_player_audio.swf"
			},
			"video.swf": {
				name: "video",
				dft: "nej_player_video.swf"
			},
			"clipboard.swf": {
				name: "clipboard",
				dft: "nej_clipboard.swf"
			}
		};
		return function(ci) {
			gn.vY("root", ci.root || "/res/");
			var wf, eg = gn.bL("root");
			for (var x in bu) {
				wf = bu[x];
				gn.vY(x, ci[wf.name] || eg + wf.dft)
			}
			var kH = ci.p_csrf;
			if (kH == !0) {
				kH = {
					cookie: "AntiCSRF",
					param: "AntiCSRF"
				}
			}
			gn.vY("csrf", NEJ.EX({
				cookie: "",
				param: ""
			}, kH));
			bF.frames = {};
			Cs(ci.p_frame, bF.frames);
			bF.flashs = {};
			Cs(ci.p_flash, bF.flashs)
		}
	}();
	gn.vY = function(bba, bA) {
		bF[bba] = bA
	};
	gn.bL = function(bba) {
		return bF[bba]
	};
	gn.Rj = function(bC) {
		var CT = zW(bC);
		return bF.frames[CT] || CT + "/res/nej_proxy_frame.html"
	};
	gn.Rx = function(bC) {
		return bF.flashs[zW(bC)]
	};
	AJ(window.NEJ_CONF || NEJ.O)
})();
(function() {
	var bh = NEJ.P,
		gn = bh("nej.c"),
		bs = bh("nej.g"),
		dn = +(new Date);
	bs.ZY = 1e4 - dn;
	bs.zP = 10001 - dn;
	bs.ZX = 10002 - dn;
	bs.Du = 10003 - dn;
	bs.Ye = 10004 - dn;
	bs.ZQ = 10005 - dn;
	bs.AF = 10006 - dn;
	bs.LK = 10007 - dn;
	bs.lS = "Content-Type";
	bs.ZO = "text/plain";
	bs.nC = "multipart/form-data";
	bs.Mh = "application/x-www-form-urlencoded";
	bs.El = gn.bL("blank.png") || "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw=="
})();
(function() {
	var bh = NEJ.P,
		fz = NEJ.R,
		bs = bh("nej.p"),
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bI = bh("nej.h");
	var fu = bs.eV.prefix,
		EN = bs.PU,
		Ui = {
			scale: "scale({x|1},{y|1})",
			rotate: "rotate({a})",
			translate: "translate({x},{y})"
		},
		WB = {
			scale: "scale3d({x|1},{y|1},{z|1})",
			rotate: "rotate3d({x},{y},{z},{a})",
			translate: "translate3d({x},{y},{z})"
		},
		pS = {
			transition: !0,
			transform: !0,
			animation: !0,
			keyframes: !0,
			box: !0,
			"box-pack": !0,
			"box-flex": !0,
			marquee: !0,
			"border-radius": !0,
			"user-select": !0
		};
	var AJ = function() {
		var ip = bI.FT();
		EN.css3d = !!ip && ip.m41 != null;
		var cA = /-([a-z])/g;
		for (var x in pS) {
			pS[Gt(x)] = pS[x]
		}
	};
	var Gt = function() {
		var cA = /-([a-z])/g;
		return function(bB) {
			bB = bB || "";
			return bB.replace(cA, function($1, $2) {
				return $2.toUpperCase()
			})
		}
	}();
	var Hl = function(bB) {
		return (!EN.css3d ? Ui : WB)[bB]
	};
	var Ht = function() {
		var cA = /\s+/;
		return function(dh) {
			dh = (dh || "").trim();
			return !!dh ? dh.split(cA) : null
		}
	}();
	var uR = function(bp, bv, dh) {
		bp = bg.bL(bp);
		if (!bp) return;
		bm.bfq(Ht(dh), function(cO) {
			bp.classList[bv](cO)
		})
	};
	bI.uE = function(bn) {
		return fz.slice.call(bn, 0)
	};
	bI.PT = function(bp) {
		return bI.uE(bp.children)
	};
	bI.Qm = function(bp, dh) {
		return bI.uE(bp.getElementsByClassName(dh))
	};
	bI.Bn = function(bp, nN) {
		uR(bp, "add", nN)
	};
	bI.Bz = function(bp, nP) {
		uR(bp, "remove", nP)
	};
	bI.ps = function(bp, nP, nN) {
		uR(bp, "remove", nP);
		uR(bp, "add", nN)
	};
	bI.vM = function(bp, dh) {
		bp = bg.bL(bp);
		if (!bp) return !1;
		var bn = bp.classList;
		if (!bn || !bn.length) return !1;
		return bm.fM(Ht(dh), function(cO) {
			return bn.contains(cO)
		}) >= 0
	};
	bI.Qn = function(bp, cO) {};
	bI.Qx = function(bp) {};
	bI.QA = function(dH, HN) {
		dH.selectionEnd = HN.end || 0;
		dH.selectionStart = HN.start || 0;
		dH.focus()
	};
	bI.QT = function(dH) {
		return {
			end: dH.selectionEnd,
			start: dH.selectionStart
		}
	};
	bI.Rq = function() {
		var xH = function(cO, bi) {
			var bp = bo.cK(bi);
			if (!bp.value) bg.cr(bp, cO)
		};
		var sL = function(cO, bi) {
			bg.cu(bo.cK(bi), cO)
		};
		return function(bp, dS, cO) {
			if (dS == 1) {
				bo.bW(bp, "blur", xH.bq(null, cO))
			}
			if (dS == 1 || dS == -1) {
				bo.bW(bp, "focus", sL.bq(null, cO))
			}
		}
	}();
	bI.SJ = function(mC) {
		return (new XMLSerializer).serializeToString(mC) || ""
	};
	bI.Te = function(lx) {
		var eg = (new DOMParser).parseFromString(lx, "text/xml").documentElement;
		return eg.nodeName == "parsererror" ? null : eg
	};
	bI.Tj = function(bp) {};
	bI.jO = function(bp) {
		return null
	};
	bI.To = function(bp) {
		return null
	};
	bI.UK = function(eo) {};
	bI.Ju = function() {
		var bO = bI.uE(arguments);
		bO[0] = bg.bL(bO[0]);
		if (!bO[0]) return null;
		bO[1] = (bO[1] || "").toLowerCase();
		if (!bO[1]) return null;
		return bO
	};
	bI.rV = function() {
		var kn = {
				touchstart: "mousedown",
				touchmove: "mousemove",
				touchend: "mouseup"
			},
			hJ = {
				transitionend: "TransitionEnd",
				animationend: "AnimationEnd",
				animationstart: "AnimationStart",
				animationiteration: "AnimationIteration"
			};
		var Vg = function(bv) {
			return (fu.evt || fu.pro) + bv
		};
		return function() {
			var bO = bI.Ju.apply(bI, arguments);
			if (!bO) return;
			var un = hJ[bO[1]],
				tz = kn[bO[1]];
			if (!!un) {
				bO[4] = bO[1];
				bO[1] = Vg(un)
			} else if (!!tz) {
				var bB = "on" + bO[1];
				if (!(bB in bO[0])) {
					bO[4] = bO[1];
					bO[1] = tz
				}
			}
			return bO
		}
	}();
	bI.Wl = function() {
		var bO = arguments;
		bO[0].addEventListener(bO[1], bO[2], !!bO[3])
	};
	bI.Ke = function() {
		var bO = arguments;
		bO[0].removeEventListener(bO[1], bO[2], !!bO[3])
	};
	bI.Yp = function(bp, bv, bf) {
		var bi = document.createEvent("Event");
		bi.initEvent(bv, !0, !0);
		NEJ.X(bi, bf);
		bp.dispatchEvent(bi)
	};
	bI.FT = function() {
		var dO = /\((.*?)\)/,
			fO = /\s*,\s*/,
			bn = ["m11", "m12", "m21", "m22", "m41", "m42"];
		var uk = function(ip) {
			var fv = {};
			if (dO.test(ip || "")) {
				bm.bfq(RegExp.$1.split(fO), function(bA, bfo) {
					fv[bn[bfo]] = bA || ""
				})
			}
			return fv
		};
		return function(ip) {
			if (!!window.CSSMatrix) return new CSSMatrix(ip);
			var bB = fu.clz + "CSSMatrix";
			if (!!window[bB]) return new window[bB](ip || "");
			return uk(ip)
		}
	}();
	bI.KF = function() {
		var cA = /\{(.*?)\}/g;
		return function(bB, bQ) {
			bQ = bQ || o;
			var hZ = Hl(bB);
			return !hZ ? "" : hZ.replace(cA, function($1, $2) {
				var bP = $2.split("|");
				return bQ[bP[0]] || bP[1] || "0"
			})
		}
	}();
	bI.Yh = function(bp, bB, bA) {
		bp.style[bI.KD(bB)] = bA
	};
	bI.KD = function() {
		var cA = /^[a-z]/,
			xJ = fu.css;
		var XX = function(bB) {
			return bB.replace(cA, function($1) {
				return xJ + $1.toUpperCase()
			})
		};
		return function(bB) {
			bB = Gt(bB);
			var XW = bI.XQ(bB, pS);
			return XW ? XX(bB) : bB
		}
	}();
	bI.XQ = function() {
		var cA = /^([a-z]+?)[A-Z]/;
		return function(bB, bQ) {
			if (!bQ[bB]) {
				if (cA.test(bB)) bB = RegExp.$1
			}
			return !!bQ[bB]
		}
	}();
	bI.XJ = function() {
		var cA = /\$<(.*?)>/gi,
			xJ = "-" + fu.css.toLowerCase() + "-";
		return function(fP) {
			return fP.replace(cA, function($1, $2) {
				var jo = $2,
					bP = jo.split("|"),
					hZ = Hl(bP[0]);
				if (!!hZ) {
					return bI.KF(bP[0], bm.fQ(bP[1]))
				}
				return !bI.XI(jo, pS) ? jo : xJ + jo
			})
		}
	}();
	bI.XI = function(bB, bQ) {
		return !!bQ[bB]
	};
	bI.XG = function(eR, fP) {
		eR.textContent = fP
	};
	bI.XF = function(eR, fP) {
		var zU = eR.sheet,
			cm = zU.cssRules.length;
		zU.insertRule(fP, cm);
		return zU.cssRules[cm]
	};
	bI.Yt = function(bp, bf) {};
	bI.As = function(At) {
		return (At || "").toLowerCase() != "transparent"
	};
	bI.Yu = function(bp) {};
	bI.Xx = function(bp, bB) {
		if (!!bp.getAttribute) return bp.getAttribute(bB);
		return ""
	};
	bI.us = function(dp) {
		bg.gC(dp)
	};
	AJ()
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bI = bh("nej.h");
	var vp = function() {
		var hq = !!document.body.classList;
		return function() {
			return hq
		}
	}();
	var Kx = function() {
		var cA = /\s+/g;
		return function(dh) {
			dh = (dh || "").trim();
			return !dh ? null : new RegExp("(\\s|^)(?:" + dh.replace(cA, "|") + ")(?=\\s|$)", "g")
		}
	}();
	bI.ps = bI.ps.fR(function(bi) {
		if (vp()) return;
		bi.stopped = !0;
		var bO = bi.args,
			bp = bg.bL(bO[0]);
		if (!bp || !bO[1] && !bO[2]) return;
		var dh = bp.className || "";
		var nN = " " + (bO[2] || ""),
			nP = Kx((bO[1] || "") + nN);
		!!nP && (dh = dh.replace(nP, "$1"));
		bp.className = (dh + nN).replace(/\s+/g, " ").trim()
	});
	bI.Bn = bI.Bn.fR(function(bi) {
		if (vp()) return;
		bi.stopped = !0;
		var bO = bi.args;
		bI.ps(bO[0], "", bO[1])
	});
	bI.Bz = bI.Bz.fR(function(bi) {
		if (vp()) return;
		bi.stopped = !0;
		var bO = bi.args;
		bI.ps(bO[0], bO[1], "")
	});
	bI.vM = bI.vM.fR(function(bi) {
		if (vp()) return;
		bi.stopped = !0;
		var bO = bi.args,
			bp = bg.bL(bO[0]);
		if (!bp) {
			bi.value = !1;
			return
		}
		var cA = Kx(bO[1]);
		bi.value = !cA ? !1 : cA.test(bp.className || "")
	})
})();
(function() {
	var bh = NEJ.P,
		bs = bh("nej.p"),
		bI = bh("nej.h");
	if (bs.xS.webkit) return;
	bI.As = function(At) {
		return !0
	}
})();
(function() {
	var bh = NEJ.P,
		bs = bh("nej.p"),
		bg = bh("nej.e"),
		bI = bh("nej.h");
	if (bs.xS.trident1) return;
	bI.rV = function() {
		var kn = {
			touchcancel: "MSPointerCancel",
			touchstart: "MSPointerDown",
			touchmove: "MSPointerMove",
			touchend: "MSPointerUp"
		};
		return bI.rV.fR(function(bi) {
			var bO = bI.Ju.apply(bI, bi.args);
			if (!bO) {
				bi.stopped = !0;
				return
			}
			var bv = kn[bO[1]];
			if (!!bv && ("on" + bv).toLowerCase() in bO[0]) {
				bO[4] = bO[1];
				bO[1] = bv;
				bi.stopped = !0;
				bi.value = bO
			}
		})
	}();
	bI.As = function(At) {
		return !0
	};
	bI.us = bI.us.fR(function(bi) {
		bi.stopped = !0;
		var dp = bi.args[0];
		bg.co(dp, "display", "none");
		try {
			dp.contentWindow.document.body.innerHTML = "&nbsp;"
		} catch (ex) {}
	})
})();
(function() {
	var bh = NEJ.P,
		bI = bh("nej.h"),
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		cz = bh("nej.x"),
		bF = {};
	var Kw = function(bp) {
		bp = bg.bL(bp);
		if (!bp || !bF[bp.id]) return;
		var Kv = !0,
			bt = bp.id;
		bm.dB(bF[bt], function() {
			Kv = !1;
			return !0
		});
		if (Kv) delete bF[bt]
	};
	bo.bW = cz.bW = function() {
		var Xp = function() {
			var bO = bI.rV.apply(bI, arguments);
			if (!bO || !bO[2]) return;
			var jg = bg.gl(bO[0]),
				gH = bF[jg] || {};
			bF[jg] = gH;
			jg = bO[4] || bO[1];
			var ls = gH[jg] || [];
			gH[jg] = ls;
			ls.push({
				type: bO[1],
				func: bO[2],
				capt: !!bO[3],
				sfun: bO[5] || bO[2]
			});
			return bO.slice(0, 4)
		};
		return function() {
			var bO = Xp.apply(null, arguments);
			if (!!bO) bI.Wl.apply(bI, bO);
			return this
		}
	}();
	bo.jB = cz.jB = function() {
		var Xo = function() {
			var jG = arguments,
				jg = bg.gl(jG[0]),
				gH = bF[jg],
				bv = (jG[1] || "").toLowerCase(),
				bi = jG[2];
			if (!gH || !bv || !bi) return;
			gH = gH[bv];
			if (!gH) return;
			var Xm = !!jG[3],
				bfo = bm.fM(gH, function(hJ) {
					return bi == hJ.sfun && Xm == hJ.capt
				});
			if (bfo < 0) return;
			var hJ = gH.splice(bfo, 1)[0];
			return !hJ ? null : [bg.bL(jg), hJ.type, hJ.func, hJ.capt]
		};
		return function() {
			var bO = Xo.apply(null, arguments);
			if (!!bO) {
				bI.Ke.apply(bI, bO);
				Kw(bO[0])
			}
			return this
		}
	}();
	bo.iW = cz.iW = function() {
		var Kt = function() {
			var jG = arguments,
				jg = bg.gl(jG[0]),
				gH = bF[jg],
				ls = (jG[1] || "").toLowerCase();
			if (!gH || !ls) return;
			var bp = bg.bL(jg);
			bm.gU(gH[ls], function(hJ, bfo, bn) {
				bI.Ke(bp, hJ.type, hJ.func, hJ.capt);
				bn.splice(bfo, 1)
			});
			delete gH[ls]
		};
		var Xi = function(bp) {
			bp = bg.bL(bp);
			if (!bp) return;
			var bt = bp.id;
			bm.dB(bF[bt], function(bn, bv) {
				Kt(bt, bv)
			});
			delete bF[bt]
		};
		return function(bp, bv) {
			!bv ? Xi(bp) : Kt(bp, bv);
			Kw(bp);
			return this
		}
	}();
	bo.cK = function() {
		var xC;
		var nO = function(bB, bp) {
			var bP = bB.split(":");
			if (bP.length > 1) {
				if (!xC) xC = {
					c: bg.eF,
					d: bg.bH,
					a: bg.fl
				};
				var tF = xC[bP[0]];
				if (!!tF) return !!tF(bp, bP[1]);
				bB = bP[1]
			}
			return !!bg.fl(bp, bB) || !!bg.bH(bp, bB) || bg.eF(bp, bB)
		};
		return function(bi) {
			if (!bi) return null;
			var bp = bi.target || bi.srcElement,
				ek = arguments[1];
			if (!ek) return bp;
			if (bm.dW(ek)) ek = nO.bq(null, ek);
			if (bm.es(ek)) {
				while (bp) {
					if (!!ek(bp)) return bp;
					bp = bp.parentNode
				}
				return null
			}
			return bp
		}
	}();
	bo.ep = function(bi) {
		bo.pk(bi);
		bo.fw(bi);
		return this
	};
	bo.pk = function(bi) {
		if (!!bi) {
			!!bi.stopPropagation ? bi.stopPropagation() : bi.cancelBubble = !0
		}
		return this
	};
	bo.fw = function(bi) {
		if (!!bi) {
			!!bi.preventDefault ? bi.preventDefault() : bi.returnValue = !1
		}
		return this
	};
	bo.Yv = function() {
		var iR = !1;
		var Xe = function() {
			if (iR) return;
			iR = !0;
			bo.bW(document, "click", WZ, !0)
		};
		var WZ = function(bi) {
			var bp = bo.cK(bi),
				WN = bg.bH(bp, "stopped");
			if (WN == "true") {
				bo.ep(bi);
				bg.bH(bp, "stopped", "false")
			}
		};
		return function(bi) {
			if (!bi) return;
			if (bi.type == "click") {
				bo.ep(bi);
				return
			}
			Xe();
			bg.bH(bo.cK(bi), "stopped", "true")
		}
	}();
	bo.oQ = function(bi) {
		return bi.pageX != null ? bi.pageX : bi.clientX + bg.ko().scrollLeft
	};
	bo.pZ = function(bi) {
		return bi.pageY != null ? bi.pageY : bi.clientY + bg.ko().scrollTop
	};
	bo.bJ = cz.bJ = function(bp, bv, bf) {
		var bO = bI.rV(bp, bv);
		if (!!bO) bI.Yp(bO[0], bO[1], bf);
		return this
	};
	bh("dbg").dumpEV = function() {
		return bF
	};
	cz.isChange = !0
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		dJ = bh("nej.g"),
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		bI = bh("nej.h"),
		cz = bh("nej.x"),
		oB, zZ = {},
		bF = document.createDocumentFragment();
	bg.gl = cz.gl = function(bp) {
		bp = bg.bL(bp);
		if (!bp) return;
		var bt = !!bp.id ? bp.id : "auto-id-" + bm.Kn(16);
		bp.id = bt;
		if (bg.bL(bt) != bp) zZ[bt] = bp;
		return bt
	};
	bg.bL = cz.bL = function(bp) {
		var bl = zZ["" + bp];
		if (!!bl) return bl;
		if (!bm.dW(bp) && !bm.tw(bp)) return bp;
		return document.getElementById(bp)
	};
	bg.gL = cz.gL = function(bp, cO) {
		bp = bg.bL(bp);
		if (!bp) return null;
		var bn = bI.PT(bp);
		if (!!cO) bm.gU(bn, function(bl, bfo) {
			if (!bg.eF(bl, cO)) bn.splice(bfo, 1)
		});
		return bn
	};
	bg.cw = cz.cw = function(bp, dh) {
		bp = bg.bL(bp);
		return !bp ? null : bI.Qm(bp, dh.trim())
	};
	bg.Kk = cz.Kk = function(bp) {
		bp = bg.bL(bp);
		if (!!bp) {
			bp = bp.parentNode;
			while (!!bp) {
				if (bp.scrollHeight > bp.clientHeight) break;
				bp = bp.parentNode
			}
			if (!!bp) return bp
		}
		var gH = document.body.scrollHeight,
			ls = document.documentElement.scrollHeight;
		return ls >= gH ? document.documentElement : document.body
	};
	bg.ko = function() {
		var Kj = function(bn) {
			var bw = 0;
			bm.bfq(bn, function(gG) {
				if (!gG) return;
				if (!bw) {
					bw = gG
				} else {
					bw = Math.min(bw, gG)
				}
			});
			return bw
		};
		return function(WH) {
			var Kf = WH || document,
				lp = Kf.body,
				lr = Kf.documentElement,
				bw = {
					scrollTop: Math.max(lp.scrollTop, lr.scrollTop),
					scrollLeft: Math.max(lp.scrollLeft, lr.scrollLeft),
					clientWidth: Kj([lp.clientWidth, lp.offsetWidth, lr.clientWidth, lr.offsetWidth]),
					clientHeight: Kj([lp.clientHeight, lp.offsetHeight, lr.clientHeight, lr.offsetHeight])
				};
			bw.scrollWidth = Math.max(bw.clientWidth, lp.scrollWidth, lr.scrollWidth);
			bw.scrollHeight = Math.max(bw.clientHeight, lp.scrollHeight, lr.scrollHeight);
			return bw
		}
	}();
	bg.Yw = function(gA, gz) {
		var bw = NEJ.X({}, gz),
			JT = gA.width / gA.height,
			ul = gz.width / gz.height;
		if (JT > ul && gz.height > gA.height) {
			bw.height = gA.height;
			bw.width = bw.height * ul
		}
		if (JT < ul && gz.width > gA.width) {
			bw.width = gA.width;
			bw.height = bw.width / ul
		}
		return bw
	};
	bg.Yy = function() {
		var cA = /\s+/;
		var jI = {
			left: function() {
				return 0
			},
			center: function(hC, gz) {
				return (hC.width - gz.width) / 2
			},
			right: function(hC, gz) {
				return hC.width - gz.width
			},
			top: function() {
				return 0
			},
			middle: function(hC, gz) {
				return (hC.height - gz.height) / 2
			},
			bottom: function(hC, gz) {
				return hC.height - gz.height
			}
		};
		return function(hC, gz, gf) {
			var bw = {},
				bP = (gf || "").split(cA),
				jP = jI[bP[1]] || jI.middle,
				wi = jI[bP[0]] || jI.center;
			bw.top = jP(hC, gz);
			bw.left = wi(hC, gz);
			return bw
		}
	}();
	bg.JR = cz.JR = function(bp, cO) {
		bI.Qn(bp, cO || bg.bH(bp, "hover") || "js-hover");
		return this
	};
	bg.JQ = cz.JQ = function(bp) {
		bp = bg.bL(bp);
		if (!bp) return;
		bI.Qx(bp)
	};
	bg.Wh = cz.Wh = function() {
		var bF = {},
			JO = 2;
		var We = function(bt, cO, bi) {
			bF[bt] = [bo.oQ(bi), bo.pZ(bi)];
			bg.cu(bt, cO)
		};
		var VX = function(bt, cO, bi) {
			var ew = bF[bt];
			if (!bm.eZ(ew)) return;
			var VV = Math.abs(bo.oQ(bi) - ew[0]),
				VU = Math.abs(bo.pZ(bi) - ew[1]);
			if (VV > JO || VU > JO) xr(bt, cO)
		};
		var xr = function(bt, cO) {
			if (bm.eZ(bF[bt])) {
				bF[bt] = -1;
				bg.cr(bt, cO)
			}
		};
		return function(bp, cO) {
			var bt = bg.gl(bp);
			if (!bt || bF[bt] != null) return;
			bF[bt] = -1;
			cO = cO || bg.bH(bt, "highlight") || "js-highlight";
			bo.bW(bt, "touchstart", We.bq(null, bt, cO));
			bo.bW(document, "touchmove", VX.bq(null, bt, cO));
			bo.bW(document, "touchend", xr.bq(null, bt, cO));
			bo.bW(document, "touchcancel", xr.bq(null, bt, cO))
		}
	}();
	bg.xt = cz.xt = function() {
		var VS = function(bt, cO, VR) {
			var bp = bg.bL(bt),
				bi = {
					clazz: cO,
					target: bp
				};
			if (bg.eF(bp, cO)) {
				bi.toggled = !1;
				bg.cr(bp, cO)
			} else {
				bi.toggled = !0;
				bg.cu(bp, cO)
			}
			VR.call(null, bi)
		};
		return function(bp, bf) {
			bp = bg.bL(bp);
			if (!!bp) {
				var fv = {
					ontoggle: bY,
					clazz: "js-toggle",
					element: bp.parentNode
				};
				if (bm.dW(bf)) {
					var bl = bg.bL(bf);
					!!bl ? fv.element = bl : fv.clazz = bf
				} else {
					NEJ.EX(fv, bf);
					fv.element = bg.bL(fv.element)
				}
				var bt = bg.gl(fv.element);
				bo.bW(bp, "click", VS.bq(null, bt, fv.clazz, fv.ontoggle || bY))
			}
			return this
		}
	}();
	bg.JJ = cz.JJ = function(bp, bf) {
		bp = bg.bL(bp);
		if (!bp) return;
		var dS = 0,
			cO = "js-focus";
		if (bm.tw(bf)) {
			dS = bf
		} else if (bm.dW(bf)) {
			cO = bf
		} else if (bm.jq(bf)) {
			dS = bf.mode || dS;
			cO = bf.clazz || cO
		}
		var bA = parseInt(bg.bH(bp, "mode"));
		if (!isNaN(bA)) dS = bA;
		bA = bg.bH(bp, "focus");
		if (!!bA) cO = bA;
		bI.Rq(bp, dS, cO);
		return this
	};
	bg.hw = function() {
		var bQ = {
			a: {
				href: "#",
				hideFocus: !0
			},
			style: {
				type: "text/css"
			},
			link: {
				type: "text/css",
				rel: "stylesheet"
			},
			iframe: {
				frameBorder: 0
			},
			script: {
				defer: !0,
				type: "text/javascript"
			}
		};
		return function(iC, dh, cl) {
			var bp = document.createElement(iC);
			NEJ.X(bp, bQ[iC.toLowerCase()]);
			if (!!dh) bp.className = dh;
			cl = bg.bL(cl);
			if (!!cl) cl.appendChild(bp);
			return bp
		}
	}();
	bg.xO = function() {
		var VP = function() {
			if (location.hostname == document.domain) return "about:blank";
			return 'javascript:(function(){document.open();document.domain="' + document.domain + '";document.close();})();'
		};
		var VN = function(bB) {
			bB = bB.trim();
			if (!bB) return bg.hw("iframe");
			var dp;
			try {
				dp = document.createElement('<iframe name="' + bB + '"></iframe>');
				dp.frameBorder = 0
			} catch (e) {
				dp = bg.hw("iframe");
				dp.name = bB
			}
			return dp
		};
		return function(bf) {
			bf = bf || bX;
			var dp = VN(bf.name || "");
			if (!bf.visible) dp.style.display = "none";
			if (bm.es(bf.onload)) bo.bW(dp, "load", function(bi) {
				if (!dp.src) return;
				bo.iW(dp, "load");
				bf.onload(bi)
			});
			var cl = bf.parent;
			if (bm.es(cl)) {
				try {
					cl(dp)
				} catch (e) {}
			} else {
				(bg.bL(cl) || document.body).appendChild(dp)
			}
			var di = bf.src || VP();
			window.setTimeout(function() {
				dp.src = di
			}, 0);
			return dp
		}
	}();
	bg.gC = cz.gC = function() {
		var JI = function(xU) {
			xU.src = dJ.El
		};
		var JG = function(lX) {
			lX.src = "about:blank"
		};
		return function(bp, VG) {
			bp = bg.bL(bp);
			if (!bp) return this;
			if (!VG) bo.iW(bp);
			delete zZ[bp.id];
			var iC = bp.tagName;
			if (iC == "IFRAME") {
				JG(bp)
			} else if (iC == "IMG") {
				JI(bp)
			} else if (!!bp.getElementsByTagName) {
				bm.bfq(bp.getElementsByTagName("img"), JI);
				bm.bfq(bp.getElementsByTagName("iframe"), JG)
			}
			if (!!bp.parentNode) {
				bp.parentNode.removeChild(bp)
			}
			return this
		}
	}();
	bg.iI = cz.iI = function(bp) {
		bp = bg.bL(bp);
		if (!!bp) bF.appendChild(bp);
		return this
	};
	bg.JF = cz.JF = function(bp) {
		bp = bg.bL(bp);
		if (!bp) return;
		bm.gU(bp.childNodes, function(bl) {
			bg.gC(bl)
		})
	};
	bg.yr = cz.yr = function() {
		var cO, dO = /\s+/;
		var VD = function() {
			if (!!cO) return;
			cO = bg.kN(".#<uispace>{position:relative;zoom:1;}.#<uispace>-show{position:absolute;top:0;left:100%;cursor:text;white-space:nowrap;overflow:hidden;}");
			bg.JE()
		};
		return function(bp, bf) {
			bp = bg.bL(bp);
			if (!bp) return;
			VD();
			bf = bf || bX;
			var cl = bp.parentNode;
			if (!bg.eF(cl, cO)) {
				cl = bg.hw("span", cO);
				bp.insertAdjacentElement("beforeBegin", cl);
				cl.appendChild(bp)
			}
			var JC = bf.nid || "",
				bl = bg.cw(cl, JC || cO + "-show")[0];
			if (!bl) {
				var cZ = ((bf.clazz || "") + " " + JC).trim();
				cZ = cO + "-show" + (!cZ ? "" : " ") + cZ;
				bl = bg.hw(bf.tag || "span", cZ);
				cl.appendChild(bl)
			}
			var cZ = bf.clazz;
			if (!!cZ) {
				cZ = (cZ || "").trim().split(dO)[0] + "-parent";
				bg.cu(cl, cZ)
			}
			return bl
		}
	}();
	bg.bH = cz.bH = function() {
		var jp = {},
			iC = "data-",
			cA = /\-(.{1})/gi;
		var mG = function(bp) {
			var bt = bg.gl(bp);
			if (!!jp[bt]) return;
			var bQ = {};
			bm.bfq(bp.attributes, function(bl) {
				var bba = bl.nodeName;
				if (bba.indexOf(iC) != 0) return;
				bba = bba.replace(iC, "").replace(cA, function($1, $2) {
					return $2.toUpperCase()
				});
				bQ[bba] = bl.nodeValue || ""
			});
			jp[bt] = bQ
		};
		return function(bp, bba, bA) {
			bp = bg.bL(bp);
			if (!bp) return null;
			var kg = bp.dataset;
			if (!kg) {
				mG(bp);
				kg = jp[bp.id]
			}
			if (bA !== undefined) kg[bba] = bA;
			return kg[bba]
		}
	}();
	bg.fl = cz.fl = function(bp, bB, bA) {
		bp = bg.bL(bp);
		if (!bp) return "";
		if (bA !== undefined && !!bp.setAttribute) bp.setAttribute(bB, bA);
		return bI.Xx(bp, bB)
	};
	bg.nt = function(gp) {
		var zV = document.createElement("div");
		zV.innerHTML = gp;
		var bn = bg.gL(zV);
		return bn.length > 1 ? zV : bn[0]
	};
	bg.Vw = cz.Vw = function(bp) {
		bp = bg.bL(bp);
		return !bp ? "" : bI.SJ(bp)
	};
	bg.JB = function(lx) {
		lx = (lx || "").trim();
		return !lx ? null : bI.Te(lx)
	};
	bg.Vu = function(dR, bv) {
		dR = dR || "";
		switch (bv) {
			case "xml":
				dR = bg.JB(dR);
				break;
			case "json":
				try {
					dR = JSON.parse(dR)
				} catch (ex) {
					dR = null
				}
				break
		}
		return dR
	};
	bg.kT = cz.kT = function() {
		var Vs = function(bp) {
			return bp == document.body || bp == document.documentElement
		};
		return function(cD, im) {
			cD = bg.bL(cD);
			if (!cD) return null;
			im = bg.bL(im) || null;
			var bw = {
					x: 0,
					y: 0
				},
				AK, cY, uq;
			while (!!cD && cD != im) {
				AK = Vs(cD);
				cY = AK ? 0 : cD.scrollLeft;
				uq = parseInt(bg.hP(cD, "borderLeftWidth")) || 0;
				bw.x += cD.offsetLeft + uq - cY;
				cY = AK ? 0 : cD.scrollTop;
				uq = parseInt(bg.hP(cD, "borderTopWidth")) || 0;
				bw.y += cD.offsetTop + uq - cY;
				cD = cD.offsetParent
			}
			return bw
		}
	}();
	bg.Jy = cz.Jy = function(bp) {
		var cj = bg.kT(bp);
		window.scrollTo(cj.x, cj.y);
		return this
	};
	bg.YC = function(ip) {
		ip = (ip || "").trim();
		return bI.FT(ip)
	};
	bg.Vm = cz.Vm = function(bp, bB, bQ) {
		bp = bg.bL(bp);
		if (!bp) return this;
		var bA = bI.KF(bB, bQ);
		if (!bA) return this;
		bg.co(bp, "transform", bA);
		return this
	};
	bg.BF = cz.BF = function(bp, bQ) {
		bp = bg.bL(bp);
		if (!!bp) bm.dB(bQ, function(bA, bB) {
			bg.co(bp, bB, bA)
		});
		return this
	};
	bg.Vk = cz.Vk = function(dH, bf) {
		dH = bg.bL(dH);
		if (!dH) return {
			start: 0,
			end: 0
		};
		if (bm.tw(bf)) bf = {
			start: bf,
			end: bf
		};
		if (bf != null) {
			if (bf.end == null) bf.end = bf.start || 0;
			bI.QA(dH, bf)
		} else {
			bf = bI.QT(dH)
		}
		return bf
	};
	bg.co = cz.co = function(bp, bB, bA) {
		bp = bg.bL(bp);
		if (!!bp) bI.Yh(bp, bB, bA);
		return this
	};
	bg.hP = cz.hP = function(bp, bB) {
		bp = bg.bL(bp);
		if (!bp) return "";
		var gQ = !window.getComputedStyle ? bp.currentStyle || bX : window.getComputedStyle(bp, null);
		return gQ[bI.KD(bB)] || ""
	};
	bg.Jw = function() {
		var cA = /[\s\r\n]+/gi;
		return function(eR) {
			eR = (eR || "").trim().replace(cA, " ");
			if (!eR) return;
			var bl = bg.hw("style");
			document.head.appendChild(bl);
			bI.XG(bl, bI.XJ(eR));
			return bl
		}
	}();
	bg.Jv = function(vf) {
		try {
			vf = vf.trim();
			if (!!vf) return (new Function(vf))()
		} catch (ex) {
			console.error(ex.message);
			console.error(ex.stack)
		}
	};
	bg.kN = function() {
		var cA = /#<.*?>/g,
			dn = +(new Date);
		return function(fP) {
			if (!oB) oB = [];
			var dh = "auto-" + dn++;
			oB.push(fP.replace(cA, dh));
			return dh
		}
	}();
	bg.JE = function() {
		if (!!oB) {
			bg.Jw(oB.join(""));
			oB = null
		}
		return this
	};
	bg.YD = function(eR, fP) {
		eR = bg.bL(eR);
		return !eR ? null : bI.XF(eR, fP)
	};
	bg.cu = cz.cu = function() {
		bI.Bn.apply(bI, arguments);
		return this
	};
	bg.cr = cz.cr = function() {
		bI.Bz.apply(bI, arguments);
		return this
	};
	bg.rB = cz.rB = function() {
		bI.ps.apply(bI, arguments);
		return this
	};
	bg.eF = cz.eF = function() {
		return bI.vM.apply(bI, arguments)
	};
	if (!document.head) document.head = document.getElementsByTagName("head")[0] || document.body;
	cz.isChange = !0
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		fz = NEJ.R,
		bY = NEJ.F,
		bg = bh("nej.e"),
		bI = bh("nej.h"),
		bm = bh("nej.u");
	var lD = function(bk, bv) {
		try {
			bv = bv.toLowerCase();
			if (bk === null) return bv == "null";
			if (bk === undefined) return bv == "undefined";
			return bX.toString.call(bk).toLowerCase() == "[object " + bv + "]"
		} catch (e) {
			return !1
		}
	};
	bm.es = function(bk) {
		return lD(bk, "function")
	};
	bm.dW = function(bk) {
		return lD(bk, "string")
	};
	bm.tw = function(bk) {
		return lD(bk, "number")
	};
	bm.YE = function(bk) {
		return lD(bk, "boolean")
	};
	bm.Jt = function(bk) {
		return lD(bk, "date")
	};
	bm.eZ = function(bk) {
		return lD(bk, "array")
	};
	bm.jq = function(bk) {
		return lD(bk, "object")
	};
	bm.rA = function() {
		var cA = /[^\x00-\xfff]/g;
		return function(bU) {
			return ("" + (bU || "")).replace(cA, "**").length
		}
	}();
	bm.fM = function(bn, bfp) {
		var ek = bm.es(bfp) ? bfp : function(bA) {
				return bA === bfp
			},
			bfo = bm.dB(bn, ek);
		return bfo != null ? bfo : -1
	};
	bm.YF = function() {
		var Js;
		var ry = function(bn, gF, gE) {
			if (gF > gE) return -1;
			var ru = Math.ceil((gF + gE) / 2),
				bw = Js(bn[ru], ru, bn);
			if (bw == 0) return ru;
			if (bw < 0) return ry(bn, gF, ru - 1);
			return ry(bn, ru + 1, gE)
		};
		return function(bn, tF) {
			Js = tF || bY;
			return ry(bn, 0, bn.length - 1)
		}
	}();
	bm.gU = function(bn, en, lN) {
		if (!bn || !bn.length || !bm.es(en)) return null;
		for (var i = bn.length - 1; i >= 0; i--)
			if (!!en.call(lN, bn[i], i, bn)) return i;
		return null
	};
	bm.bfq = function(bn, en, lN) {
		if (!bn || !bn.length || !bm.es(en)) return this;
		if (!!bn.forEach) {
			bn.forEach(en, lN);
			return this
		}
		for (var i = 0, l = bn.length; i < l; i++) en.call(lN, bn[i], i, bn);
		return this
	};
	bm.dB = function(bn, en, lN) {
		if (!bn || !bm.es(en)) return null;
		if (bn.length != null) {
			if (bn.length > 0)
				for (var i = 0, l = bn.length; i < l; i++)
					if (!!en.call(lN, bn[i], i, bn)) return i
		}
		if (bm.jq(bn)) {
			for (var x in bn)
				if (bn.hasOwnProperty(x) && !!en.call(lN, bn[x], x, bn)) return x
		}
		return null
	};
	bm.UQ = function(mV, UP, bf) {
		mV = mV || [];
		bf = bf || bX;
		var Jp = !!bf.union,
			ji = !!bf.begin,
			uI = bf.compare,
			UL = Jp && ji ? bm.gU : bm.bfq;
		UL(UP, function(bfp) {
			if (!!uI) uI = uI.jh(bfp);
			var bfo = bm.fM(mV, uI || bfp);
			if (bfo >= 0) mV.splice(bfo, 1);
			if (Jp) mV[ji ? "unshift" : "push"](bfp)
		});
		return mV
	};
	bm.rl = function(bQ, bU) {
		if (!bQ || !bU || !bU.replace) return bU || "";
		return bU.replace(bQ.r, function($1) {
			var bw = bQ[!bQ.i ? $1.toLowerCase() : $1];
			return bw != null ? bw : $1
		})
	};
	bm.jS = function() {
		var bQ = {
			r: /\<|\>|\&lt;|\&gt;|\&|\r|\n|\s|\'|\"/g,
			"<": "&lt;",
			">": "&gt;",
			"&": "&amp;",
			" ": "&nbsp;",
			'"': "&quot;",
			"'": "&#39;",
			"\n": "<br/>",
			"\r": "",
			"&lt;": "&lt;",
			"&gt;": "&gt;"
		};
		return function(bU) {
			return bm.rl(bQ, bU)
		}
	}();
	bm.YG = function() {
		var bQ = {
			r: /\&(?:lt|gt|amp|nbsp|#39|quot)\;|\<br\/\>/gi,
			"&lt;": "<",
			"&gt;": ">",
			"&amp;": "&",
			"&nbsp;": " ",
			"&#39;": "'",
			"&quot;": '"',
			"<br/>": "\n"
		};
		return function(bU) {
			return bm.rl(bQ, bU)
		}
	}();
	bm.rk = function() {
		var bQ = {
				i: !0,
				r: /\byyyy|yy|MM|cM|eM|M|dd|d|HH|H|mm|ms|ss|m|s|w|ct|et\b/g
			},
			UJ = ["上午", "下午"],
			UI = ["A.M.", "P.M."],
			UF = ["日", "一", "二", "三", "四", "五", "六"],
			UD = ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],
			UA = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];
		var qY = function(dP) {
			dP = parseInt(dP) || 0;
			return (dP < 10 ? "0" : "") + dP
		};
		var Ut = function(ni) {
			return ni < 12 ? 0 : 1
		};
		return function(dg, Jk, Uq) {
			if (!dg || !Jk) return "";
			dg = bm.Uo(dg);
			bQ.yyyy = dg.getFullYear();
			bQ.yy = ("" + bQ.yyyy).substr(2);
			bQ.M = dg.getMonth() + 1;
			bQ.MM = qY(bQ.M);
			bQ.eM = UA[bQ.M - 1];
			bQ.cM = UD[bQ.M - 1];
			bQ.d = dg.getDate();
			bQ.dd = qY(bQ.d);
			bQ.H = dg.getHours();
			bQ.HH = qY(bQ.H);
			bQ.m = dg.getMinutes();
			bQ.mm = qY(bQ.m);
			bQ.s = dg.getSeconds();
			bQ.ss = qY(bQ.s);
			bQ.ms = dg.getMilliseconds();
			bQ.w = UF[dg.getDay()];
			var IZ = Ut(bQ.H);
			bQ.ct = UJ[IZ];
			bQ.et = UI[IZ];
			if (!!Uq) {
				bQ.H = bQ.H % 12
			}
			return bm.rl(bQ, Jk)
		}
	}();
	bm.Uo = function(dg) {
		var lK = dg;
		if (bm.dW(dg)) lK = new Date(Date.parse(dg));
		if (!bm.Jt(dg)) lK = new Date(dg);
		return lK
	};
	bm.JQ = function(TY, TU) {
		return (new Number(TY)).toFixed(TU)
	};
	bm.yH = function() {
		var dO = /([^\/:])\/.*$/,
			fO = /\/[^\/]+$/,
			qR = /[#\?]/,
			qQ = location.href.split(/[?#]/)[0],
			yX = document.createElement("a");
		var zf = function(fH) {
			return (fH || "").indexOf("://") > 0
		};
		var IX = function(fH) {
			return (fH || "").split(qR)[0].replace(fO, "/")
		};
		var TR = function(fH, eg) {
			if (fH.indexOf("/") == 0) return eg.replace(dO, "$1") + fH;
			return IX(eg) + fH
		};
		qQ = IX(qQ);
		return function(fH, eg) {
			fH = (fH || "").trim();
			if (!zf(eg)) eg = qQ;
			if (!fH) return eg;
			if (zf(fH)) return fH;
			fH = TR(fH, eg);
			yX.href = fH;
			fH = yX.href;
			return zf(fH) ? fH : yX.getAttribute("href", 4)
		}
	}();
	bm.TI = function() {
		var cA = /^([\w]+?:\/\/.*?(?=\/|$))/i;
		return function(bC) {
			if (cA.test(bC || "")) return RegExp.$1.toLowerCase();
			return ""
		}
	}();
	bm.IQ = function(mC, fv) {
		if (!mC) return fv;
		var bB = mC.tagName.toLowerCase(),
			bn = bg.gL(mC);
		if (!bn || !bn.length) {
			fv[bB] = mC.textContent || mC.text || "";
			return fv
		}
		var dN = {};
		fv[bB] = dN;
		bm.bfq(bn, function(bl) {
			bm.IQ(bl, dN)
		});
		return fv
	};
	bm.YI = function(lx) {
		try {
			return bm.IQ(bg.JB(lx), {})
		} catch (ex) {
			return null
		}
	};
	bm.TB = function(fg, qN) {
		var fv = {};
		bm.bfq((fg || "").split(qN), function(bB) {
			var tN = bB.split("=");
			if (!tN || !tN.length) return;
			var bba = tN.shift();
			if (!bba) return;
			fv[decodeURIComponent(bba)] = decodeURIComponent(tN.join("="))
		});
		return fv
	};
	bm.IM = function(ds, qN, Ty) {
		if (!ds) return "";
		var bP = [];
		for (var x in ds) {
			bP.push(encodeURIComponent(x) + "=" + (!!Ty ? encodeURIComponent(ds[x]) : ds[x]))
		}
		return bP.join(qN || ",")
	};
	bm.fQ = function(cn) {
		return bm.TB(cn, "&")
	};
	bm.fr = function(ds) {
		return bm.IM(ds, "&", !0)
	};
	bm.YJ = function(ds) {
		return bI.uE(ds)
	};
	bm.YL = function(bn, ek) {
		var bw = {};
		bm.bfq(bn, function(bfp) {
			var bba = bfp;
			if (!!ek) {
				bba = ek(bfp)
			}
			bw[bba] = bfp
		});
		return bw
	};
	bm.YM = function(dP, dF) {
		var Tn = ("" + dP).length,
			Tm = Math.max(1, parseInt(dF) || 0),
			cY = Tm - Tn;
		if (cY > 0) {
			dP = (new Array(cY + 1)).join("0") + dP
		}
		return "" + dP
	};
	bm.Ay = function(ds, bB) {
		if (!bm.eZ(bB)) {
			try {
				delete ds[bB]
			} catch (e) {
				ds[bB] = undefined
			}
			return this
		}
		bm.bfq(bB, bm.Ay.bq(bm, ds));
		return this
	};
	bm.Kn = function() {
		var IG = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
		return function(cm) {
			cm = cm || 10;
			var bw = [];
			for (var i = 0, IF; i < cm; ++i) {
				IF = Math.floor(Math.random() * IG.length);
				bw.push(IG.charAt(IF))
			}
			return bw.join("")
		}
	}();
	bm.SY = function(il, gA) {
		return Math.floor(Math.random() * (gA - il) + il)
	};
	bm.ml = function(cm) {
		cm = Math.max(0, Math.min(cm || 8, 30));
		var il = Math.pow(10, cm - 1),
			gA = il * 10;
		return bm.SY(il, gA).toString()
	};
	bm.ID = function() {
		var dn = +(new Date);
		return function() {
			return "" + dn++
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		fz = NEJ.R,
		bY = NEJ.F,
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bs = bh("nej.ut"),
		gr;
	if (!!bs.dE) return;
	bs.dE = NEJ.C();
	gr = bs.dE.prototype;
	bs.dE.bZ = function(bf) {
		bf = bf || {};
		var cq = !!this.qx && this.qx.shift();
		if (!cq) {
			cq = new this(bf);
			this.SW = (this.SW || 0) + 1
		}
		cq.cP(bf);
		return cq
	};
	bs.dE.cF = function() {
		var qv = function(bfp, bfo, bn) {
			bfp.cF();
			bn.splice(bfo, 1)
		};
		return function(cq) {
			if (!cq) return null;
			if (!bm.eZ(cq)) {
				if (!(cq instanceof this)) {
					var dh = cq.constructor;
					if (!!dh.cF) dh.cF(cq);
					return null
				}
				if (cq == this.eG) delete this.eG;
				if (cq == this.kL) delete this.kL;
				cq.dx();
				if (!this.qx) this.qx = [];
				if (bm.fM(this.qx, cq) < 0) {
					this.qx.push(cq)
				}
				return null
			}
			bm.gU(cq, qv, this)
		}
	}();
	bs.dE.nu = function(bf) {
		bf = bf || {};
		if (!this.eG) this.eG = this.bZ(bf);
		return this.eG
	};
	bs.dE.SV = function(bf, qr) {
		bf = bf || {};
		if (!!qr && !!this.kL) {
			this.kL.cF();
			delete this.kL
		}
		if (!this.kL) {
			this.kL = this.bZ(bf)
		} else {
			this.kL.cP(bf)
		}
		return this.kL
	};
	gr.cI = function() {
		var dn = +(new Date);
		return function() {
			this.id = dn++;
			this.ft = {};
			this.IC = {}
		}
	}();
	gr.cP = function(bf) {
		this.vw(bf)
	};
	gr.dx = function() {
		this.iW();
		this.SM()
	};
	gr.eO = function() {
		var dn = +(new Date);
		var SL = function(bO) {
			if (!bO || bO.length < 3) return;
			this.IC["de-" + dn++] = bO;
			bo.bW.apply(bo, bO)
		};
		return function(bn) {
			bm.bfq(bn, SL, this)
		}
	}();
	gr.SM = function() {
		var SK = function(bO, bba, bQ) {
			delete bQ[bba];
			bo.jB.apply(bo, bO)
		};
		return function() {
			bm.dB(this.IC, SK)
		}
	}();
	gr.YR = function(ek) {
		ek = ek || bY;
		bm.dB(this, function(lH, bba, bQ) {
			if (!!lH && !!lH.cF && !ek(lH)) {
				delete bQ[bba];
				lH.cF()
			}
		})
	};
	gr.cF = function() {
		this.constructor.cF(this)
	};
	gr.YS = function(bv) {
		var bi = this.ft[bv.toLowerCase()];
		return !!bi && bi !== bY
	};
	gr.bW = function(bv, bi) {
		this.nz.apply(this, arguments);
		return this
	};
	gr.jB = function(bv, bi) {
		var bv = (bv || "").toLowerCase(),
			cJ = this.ft[bv];
		if (!bm.eZ(cJ)) {
			if (cJ == bi) delete this.ft[bv];
			return
		}
		bm.gU(cJ, function(lO, bfo, bn) {
			if (lO == bi) bn.splice(bfo, 1)
		})
	};
	gr.nz = function(bv, bi) {
		if (!!bv && bm.es(bi)) this.ft[bv.toLowerCase()] = bi;
		return this
	};
	gr.vw = function() {
		var SH = function(bi, bv) {
			this.nz(bv, bi)
		};
		return function(cJ) {
			bm.dB(cJ, SH, this);
			return this
		}
	}();
	gr.iW = function() {
		var wv = function(bi, bv) {
			this.iW(bv)
		};
		return function(bv) {
			var bv = (bv || "").toLowerCase();
			if (!!bv) {
				delete this.ft[bv]
			} else {
				bm.dB(this.ft, wv, this)
			}
			return this
		}
	}();
	gr.SE = function(bv, bi) {
		if (!bv || !bm.es(bi)) return this;
		bv = bv.toLowerCase();
		var cJ = this.ft[bv];
		if (!cJ) {
			this.ft[bv] = bi;
			return this
		}
		if (!bm.eZ(cJ)) {
			this.ft[bv] = [cJ]
		}
		this.ft[bv].push(bi);
		return this
	};
	gr.bJ = function(bv) {
		var bi = this.ft[(bv || "").toLowerCase()];
		if (!bi) return this;
		var bO = fz.slice.call(arguments, 1);
		if (!bm.eZ(bi)) return bi.apply(this, bO);
		bm.bfq(bi, function(cL) {
			try {
				cL.apply(this, bO)
			} catch (ex) {
				console.error(ex.message);
				console.error(ex.stack)
			}
		}, this);
		return this
	};
	bs.sG = function() {
		var bF = {};
		return function(bt, dh, bf) {
			var dm = dh.SD;
			if (!dm) {
				dm = bm.Kn(10);
				dh.SD = dm
			}
			var bba = bt + "-" + dm,
				cq = bF[bba];
			if (!!bf && !cq) {
				cq = dh.bZ(bf);
				cq.cF = cq.cF.fR(function(bi) {
					delete bF[bba];
					delete cq.cF
				});
				bF[bba] = cq
			}
			return cq
		}
	}()
})();
(function() {
	if (typeof TrimPath === "undefined") {
		TrimPath = {};
		if (typeof exports !== "undefined") TrimPath = exports
	}
	var qh = {},
		sz = [],
		Iu = /\s+/g,
		dn = +(new Date),
		nB, ci, kx;
	var mw = function() {
		var dO = /^\s*[\[\{'"].*?[\]\}'"]\s*$/,
			fO = /[\&\|\<\>\+\-\*\/\%\,\(\)\[\]\?\:\!\=\;\s]/,
			qR = /^(?:defined|null|undefined|true|false|instanceof|new|this|typeof|\$v|[\d]+)$/i,
			SA = /^new\s+/,
			Sz = /['"]/;
		var Sx = function(bA) {
			if (dO.test(bA)) return;
			bA = bA.split(".")[0].trim();
			if (!bA || Sz.test(bA)) return;
			bA = bA.replace(SA, "");
			try {
				if (qR.test(bA)) return;
				kx[bA] = 1
			} catch (e) {}
		};
		return function(bU) {
			bU = bU || "";
			if (!bU || dO.test(bU)) return;
			var bP = bU.split(fO);
			for (var i = 0, l = bP.length; i < l; i++) Sx(bP[i])
		}
	}();
	var Sw = function(cv) {
		if (cv[2] != "in") throw "bad for loop statement: " + cv.join(" ");
		sz.push(cv[1]);
		mw(cv[3]);
		return "var __HASH__" + cv[1] + " = " + cv[3] + "," + cv[1] + "," + cv[1] + "_count=0;" + "if (!!__HASH__" + cv[1] + ")" + "for(var " + cv[1] + "_key in __HASH__" + cv[1] + "){" + cv[1] + " = __HASH__" + cv[1] + "[" + cv[1] + "_key];" + "if (typeof(" + cv[1] + ')=="function") continue;' + cv[1] + "_count++;"
	};
	var Sv = function() {
		var cv = sz[sz.length - 1];
		return "}; if(!__HASH__" + cv + "||!" + cv + "_count){"
	};
	var St = function() {
		sz.pop();
		return "};"
	};
	var Sr = function(cv) {
		if (cv[2] != "as") throw "bad for list loop statement: " + cv.join(" ");
		var pQ = cv[1].split("..");
		if (pQ.length > 1) {
			mw(pQ[0]);
			mw(pQ[1]);
			return "for(var " + cv[3] + "," + cv[3] + "_index=0," + cv[3] + "_beg=" + pQ[0] + "," + cv[3] + "_end=" + pQ[1] + "," + cv[3] + "_length=parseInt(" + cv[3] + "_end-" + cv[3] + "_beg+1);" + cv[3] + "_index<" + cv[3] + "_length;" + cv[3] + "_index++){" + cv[3] + " = " + cv[3] + "_beg+" + cv[3] + "_index;"
		} else {
			mw(cv[1]);
			return "for(var __LIST__" + cv[3] + " = " + cv[1] + "," + cv[3] + "," + cv[3] + "_index=0," + cv[3] + "_length=__LIST__" + cv[3] + ".length;" + cv[3] + "_index<" + cv[3] + "_length;" + cv[3] + "_index++){" + cv[3] + " = __LIST__" + cv[3] + "[" + cv[3] + "_index];"
		}
	};
	var Sp = function(cv) {
		if (!cv || !cv.length) return;
		cv.shift();
		var bB = cv[0].split("(")[0];
		return "var " + bB + " = function" + cv.join("").replace(bB, "") + "{var __OUT=[];"
	};
	var Sn = function(cv) {
		if (!cv[1]) throw "bad include statement: " + cv.join(" ");
		return 'if (typeof inline == "function"){__OUT.push(inline('
	};
	var xG = function(fu, cv) {
		mw(cv.slice(1).join(" "));
		return fu
	};
	var Sm = function(cv) {
		return xG("if(", cv)
	};
	var Sk = function(cv) {
		return xG("}else if(", cv)
	};
	var Sj = function(cv) {
		return xG("var ", cv)
	};
	ci = {
		blk: /^\{(cdata|minify|eval)/i,
		tag: "forelse|for|list|if|elseif|else|var|macro|break|notrim|trim|include",
		def: {
			"if": {
				pfix: Sm,
				sfix: "){",
				pmin: 1
			},
			"else": {
				pfix: "}else{"
			},
			elseif: {
				pfix: Sk,
				sfix: "){",
				pdft: "true"
			},
			"/if": {
				pfix: "}"
			},
			"for": {
				pfix: Sw,
				pmin: 3
			},
			forelse: {
				pfix: Sv
			},
			"/for": {
				pfix: St
			},
			list: {
				pfix: Sr,
				pmin: 3
			},
			"/list": {
				pfix: "};"
			},
			"break": {
				pfix: "break;"
			},
			"var": {
				pfix: Sj,
				sfix: ";"
			},
			macro: {
				pfix: Sp
			},
			"/macro": {
				pfix: 'return __OUT.join("");};'
			},
			trim: {
				pfix: function() {
					nB = !0
				}
			},
			"/trim": {
				pfix: function() {
					nB = null
				}
			},
			inline: {
				pfix: Sn,
				pmin: 1,
				sfix: "));}"
			}
		},
		ext: {
			seed: function(fu) {
				return (fu || "") + "" + dn
			},
			"default": function(bA, ku) {
				return bA || ku
			}
		}
	};
	var Sf = function() {
		var Se = /\\([\{\}])/g;
		return function(bU, eT) {
			bU = bU.replace(Se, "$1");
			var cv = bU.slice(1, -1).split(Iu),
				bu = ci.def[cv[0]];
			if (!bu) {
				rO(bU, eT);
				return
			}
			if (!!bu.pmin && bu.pmin >= cv.length) throw "Statement needs more parameters:" + bU;
			eT.push(!!bu.pfix && typeof bu.pfix != "string" ? bu.pfix(cv) : bu.pfix || "");
			if (!!bu.sfix) {
				if (cv.length <= 1) {
					if (!!bu.pdft) eT.push(bu.pdft)
				} else {
					for (var i = 1, l = cv.length; i < l; i++) {
						if (i > 1) eT.push(" ");
						eT.push(cv[i])
					}
				}
				eT.push(bu.sfix)
			}
		}
	}();
	var Ip = function(nI, eT) {
		if (!nI || !nI.length) return;
		if (nI.length == 1) {
			var xV = nI.pop();
			mw(xV);
			eT.push(xV == "" ? '""' : xV);
			return
		}
		var xX = nI.pop().split(":");
		eT.push("__MDF['" + xX.shift() + "'](");
		Ip(nI, eT);
		if (xX.length > 0) {
			var bO = xX.join(":");
			mw(bO);
			eT.push("," + bO)
		}
		eT.push(")")
	};
	var rO = function(bU, eT) {
		if (!bU) return;
		var mi = bU.split("\n");
		if (!mi || !mi.length) return;
		for (var i = 0, l = mi.length, gh; i < l; i++) {
			gh = mi[i];
			if (!!nB) {
				gh = gh.trim();
				if (!gh) continue
			}
			RY(gh, eT);
			if (!!nB && i < l - 1) eT.push("__OUT.push('\\n');")
		}
	};
	var RY = function() {
		var RX = /\|\|/g,
			RW = /#@@#/g;
		return function(bU, eT) {
			var pK = "}",
				pJ = -1,
				cm = bU.length,
				ji, iq, nK, uU, pH;
			while (pJ + pK.length < cm) {
				ji = "${";
				iq = "}";
				nK = bU.indexOf(ji, pJ + pK.length);
				if (nK < 0) break;
				if (bU.charAt(nK + 2) == "%") {
					ji = "${%";
					iq = "%}"
				}
				uU = bU.indexOf(iq, nK + ji.length);
				if (uU < 0) break;
				uO(bU.substring(pJ + pK.length, nK), eT);
				pH = bU.substring(nK + ji.length, uU).replace(RX, "#@@#").split("|");
				for (var i = 0, l = pH.length; i < l; pH[i] = pH[i].replace(RW, "||"), i++);
				eT.push("__OUT.push(");
				Ip(pH, eT);
				eT.push(");");
				pK = iq;
				pJ = uU
			}
			uO(bU.substring(pJ + pK.length), eT)
		}
	}();
	var uO = function() {
		var bQ = {
			r: /\n|\\|\'/g,
			"\n": "\\n",
			"\\": "\\\\",
			"'": "\\'"
		};
		var RU = function(bU) {
			return (bU || "").replace(bQ.r, function($1) {
				return bQ[$1] || $1
			})
		};
		return function(bU, eT) {
			if (!bU) return;
			eT.push("__OUT.push('" + RU(bU) + "');")
		}
	}();
	var RR = function() {
		var RP = /\t/g,
			RO = /\n/g,
			RM = /\r\n?/g;
		var Il = function(bU, ji) {
			var bfo = bU.indexOf("}", ji + 1);
			while (bU.charAt(bfo - 1) == "\\") {
				bfo = bU.indexOf("}", bfo + 1)
			}
			return bfo
		};
		var RJ = function() {
			var bP = [],
				RI = arguments[0];
			for (var x in RI) {
				x = (x || "").trim();
				if (!x) continue;
				bP.push(x + "=$v('" + x + "')")
			}
			return bP.length > 0 ? "var " + bP.join(",") + ";" : ""
		};
		return function(bU) {
			kx = {};
			bU = bU.replace(RM, "\n").replace(RP, "    ");
			var hH = ["if(!__CTX) return '';", ""];
			hH.push("function $v(__NAME){var v = __CTX[__NAME];return v==null?window[__NAME]:v;};");
			hH.push("var defined=function(__NAME){return __CTX[__NAME]!=null;},");
			hH.push("__OUT=[];");
			var lE = -1,
				cm = bU.length;
			var fN, nM, pE, pz, kY, px, zT, pw;
			while (lE + 1 < cm) {
				fN = lE;
				fN = bU.indexOf("{", fN + 1);
				while (fN >= 0) {
					nM = Il(bU, fN);
					pE = bU.substring(fN, nM);
					pz = pE.match(ci.blk);
					if (!!pz) {
						kY = pz[1].length + 1;
						px = bU.indexOf("}", fN + kY);
						if (px >= 0) {
							zT = px - fN - kY <= 0 ? "{/" + pz[1] + "}" : pE.substr(kY + 1);
							kY = bU.indexOf(zT, px + 1);
							if (kY >= 0) {
								rO(bU.substring(lE + 1, fN), hH);
								pw = bU.substring(px + 1, kY);
								switch (pz[1]) {
									case "cdata":
										uO(pw, hH);
										break;
									case "minify":
										uO(pw.replace(RO, " ").replace(Iu, " "), hH);
										break;
									case "eval":
										if (!!pw) hH.push("__OUT.push((function(){" + pw + "})());");
										break
								}
								fN = lE = kY + zT.length - 1
							}
						}
					} else if (bU.charAt(fN - 1) != "$" && bU.charAt(fN - 1) != "\\" && pE.substr(pE.charAt(1) == "/" ? 2 : 1).search(ci.tag) == 0) {
						break
					}
					fN = bU.indexOf("{", fN + 1)
				}
				if (fN < 0) break;
				nM = Il(bU, fN);
				if (nM < 0) break;
				rO(bU.substring(lE + 1, fN), hH);
				Sf(bU.substring(fN, nM + 1), hH);
				lE = nM
			}
			rO(bU.substring(lE + 1), hH);
			hH.push(';return __OUT.join("");');
			hH[1] = RJ(kx);
			kx = null;
			return new Function("__CTX", "__MDF", hH.join(""))
		}
	}();
	TrimPath.seed = function() {
		return dn
	};
	TrimPath.merge = function() {
		var pr = {};
		TrimPath.dump = function() {
			return {
				func: pr,
				text: qh
			}
		};
		return function(dm, bk, hp) {
			try {
				bk = bk || {};
				if (!pr[dm] && !qh[dm]) return "";
				if (!pr[dm]) {
					pr[dm] = RR(qh[dm]);
					delete qh[dm]
				}
				if (!!hp) {
					for (var x in ci.ext)
						if (!hp[x]) hp[x] = ci.ext[x]
				}
				return pr[dm](bk, hp || ci.ext)
			} catch (ex) {
				return ex.message || ""
			}
		}
	}();
	TrimPath.parse = function() {
		var RG = +(new Date);
		return function(bU, dm) {
			if (!bU) return "";
			dm = dm || "ck_" + RG++;
			qh[dm] = bU;
			return dm
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		tG = {};
	bg.Ag = TrimPath.seed;
	bg.eH = function() {
		var Ij = function(bt) {
			return !bg.nU ? "" : bg.nU(bt)
		};
		return function(dm, bk, hp) {
			bk = bk || {};
			bk.inline = Ij;
			hp = NEJ.X(NEJ.X({}, tG), hp);
			hp.rand = bm.ml;
			hp.format = bm.rk;
			hp.escape = bm.jS;
			hp.inline = Ij;
			return TrimPath.merge(dm, bk, hp)
		}
	}();
	bg.ky = function(bU, RC) {
		if (!bU) return "";
		var dm, bp = bg.bL(bU);
		if (!!bp) {
			dm = bp.id;
			bU = bp.value || bp.innerText;
			if (!RC) bg.gC(bp)
		}
		return TrimPath.parse(bU, dm)
	};
	bg.gX = function(cl, dm, bk, hp) {
		cl = bg.bL(cl);
		if (!!cl) cl.innerHTML = bg.eH(dm, bk, hp);
		return this
	};
	bg.YT = function(bQ) {
		NEJ.X(tG, bQ)
	};
	bh("dbg").dumpJST = function() {
		return TrimPath.dump()
	}
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bs = bh("nej.ut"),
		bj;
	if (!!bs.hk) return;
	bs.hk = NEJ.C();
	bj = bs.hk.cg(bs.dE);
	bj.cI = function() {
		this.cs = {};
		this.dv()
	};
	bj.cP = function(bf) {
		this.cS(bf);
		this.pi = bg.bL(bf.element) || window;
		this.Ig(bf.event);
		this.Rz();
		this.bJ("oninit")
	};
	bj.dx = function() {
		var mA = function(bA, bba, bQ) {
			if (!bm.eZ(bA)) {
				bm.Ay(this.pi, bba)
			}
			delete bQ[bba]
		};
		return function() {
			this.dA();
			bm.dB(this.cs, mA, this);
			delete this.pi
		}
	}();
	bj.sF = function(bp, bv) {
		bp = bg.bL(bp);
		return bp == this.pi && (!bv || !!this.cs["on" + bv])
	};
	bj.Ig = function(bi) {
		if (bm.dW(bi)) {
			var bB = "on" + bi;
			if (!this.cs[bB]) {
				this.cs[bB] = this.Ry.bq(this, bi)
			}
			this.If(bi);
			return
		}
		if (bm.eZ(bi)) {
			bm.bfq(bi, this.Ig, this)
		}
	};
	bj.If = function(bv) {
		var bi = "on" + bv,
			cL = this.pi[bi],
			Ie = this.cs[bi];
		if (cL != Ie) {
			this.sA(bv);
			if (!!cL && cL != bY) this.HZ(bv, cL);
			this.pi[bi] = Ie
		}
	};
	bj.HZ = function(bv, cL, Ru) {
		var bn = this.cs[bv];
		if (!bn) {
			bn = [];
			this.cs[bv] = bn
		}
		if (bm.es(cL)) {
			!Ru ? bn.push(cL) : bn.unshift(cL)
		}
	};
	bj.sA = function(bv, cL) {
		var bn = this.cs[bv];
		if (!bn || !bn.length) return;
		if (!cL) {
			delete this.cs[bv];
			return
		}
		bm.gU(bn, function(bA, bfo, nZ) {
			if (cL === bA) {
				nZ.splice(bfo, 1);
				return !0
			}
		})
	};
	bj.Ry = function(bv, bi) {
		bi = bi || {
			noargs: !0
		};
		bi.type = bv;
		this.bJ("ondispatch", bi);
		if (!!bi.stopped) return;
		bm.bfq(this.cs[bv], function(cL) {
			try {
				cL(bi)
			} catch (ex) {
				console.error(ex.message);
				console.error(ex.stack)
			}
		})
	};
	bj.Rz = function() {
		var Bv = function(bi) {
			var bO = bi.args,
				bv = bO[1].toLowerCase();
			if (this.sF(bO[0], bv)) {
				bi.stopped = !0;
				this.If(bv);
				this.HZ(bv, bO[2], bO[3]);
				this.bJ("oneventadd", {
					type: bv,
					listener: bO[2]
				})
			}
		};
		var Rt = function(bi) {
			var bO = bi.args,
				bv = bO[1].toLowerCase();
			if (this.sF(bO[0], bv)) {
				bi.stopped = !0;
				this.sA(bv, bO[2])
			}
		};
		var wv = function(bi) {
			var bO = bi.args,
				bv = (bO[1] || "").toLowerCase();
			if (this.sF(bO[0])) {
				if (!!bv) {
					this.sA(bv);
					return
				}
				bm.dB(this.cs, function(bA, bba) {
					if (bm.eZ(bA)) {
						this.sA(bba)
					}
				}, this)
			}
		};
		var Rs = function(bi) {
			var bO = bi.args,
				bv = bO[1].toLowerCase();
			if (this.sF(bO[0], bv)) {
				bi.stopped = !0;
				bO[0]["on" + bv].apply(bO[0], bO.slice(2))
			}
		};
		return function() {
			if (!!this.Rr) return;
			this.Rr = true;
			bo.bW = bo.bW.fR(Bv.bq(this));
			bo.jB = bo.jB.fR(Rt.bq(this));
			bo.iW = bo.iW.fR(wv.bq(this));
			bo.bJ = bo.bJ.fR(Rs.bq(this))
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		dJ = bh("nej.g"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bR = bh("nej.ut"),
		bs = bh("nej.ut.j"),
		fA, Ro = 6e4;
	if (!!bs.oT) return;
	bs.oT = NEJ.C();
	fA = bs.oT.cg(bR.dE);
	fA.cI = function() {
		this.dv();
		this.oS = {
			onerror: this.Rm.bq(this),
			onloaded: this.Rl.bq(this)
		};
		if (!this.constructor.cs) this.constructor.cs = {
			loaded: {}
		}
	};
	fA.cP = function(bf) {
		this.cS(bf);
		this.oo = bf.version;
		this.vx = bf.timeout;
		this.oS.version = this.oo;
		this.oS.timeout = this.vx
	};
	fA.HY = function(bba) {
		delete this.constructor.cs[bba]
	};
	fA.lw = function(bba) {
		return this.constructor.cs[bba]
	};
	fA.HX = function(bba, bk) {
		this.constructor.cs[bba] = bk
	};
	fA.bfH = bY;
	fA.HW = function(dq) {
		bo.iW(dq)
	};
	fA.vR = function(dq) {
		dq.src = this.gq;
		document.head.appendChild(dq)
	};
	fA.Re = function() {
		var bF = this.lw(this.gq);
		if (!bF) return;
		window.clearTimeout(bF.timer);
		this.HW(bF.request);
		delete bF.bind;
		delete bF.timer;
		delete bF.request;
		this.HY(this.gq);
		this.lw("loaded")[this.gq] = !0
	};
	fA.tZ = function(bB) {
		var bF = this.lw(this.gq);
		if (!bF) return;
		var bn = bF.bind;
		this.Re();
		if (!!bn && bn.length > 0) {
			var cq;
			while (bn.length) {
				cq = bn.shift();
				try {
					cq.bJ(bB, arguments[1])
				} catch (ex) {
					console.error(ex.message);
					console.error(ex.stack)
				}
				cq.cF()
			}
		}
	};
	fA.lu = function(eL) {
		this.tZ("onerror", eL)
	};
	fA.HV = function() {
		this.tZ("onloaded")
	};
	fA.QV = function(bC) {
		this.constructor.bZ(this.oS).tK(bC)
	};
	fA.HS = function(eL) {
		var bF = this.lw(this.ju);
		if (!bF) return;
		if (!!eL) bF.error++;
		bF.loaded++;
		if (bF.loaded < bF.total) return;
		this.HY(this.ju);
		this.bJ(bF.error > 0 ? "onerror" : "onloaded")
	};
	fA.Rm = function(eL) {
		this.HS(!0)
	};
	fA.Rl = function() {
		this.HS()
	};
	fA.tK = function(bC) {
		bC = bm.yH(bC);
		if (!bC) {
			this.bJ("onerror", {
				code: dJ.zP,
				message: "请指定要载入的资源地址！"
			});
			return this
		}
		this.gq = bC;
		if (!!this.oo) this.gq += (this.gq.indexOf("?") < 0 ? "?" : "&") + this.oo;
		if (this.lw("loaded")[this.gq]) {
			try {
				this.bJ("onloaded")
			} catch (ex) {
				console.error(ex.message);
				console.error(ex.stack)
			}
			this.cF();
			return this
		}
		var bF = this.lw(this.gq),
			dq;
		if (!!bF) {
			bF.bind.unshift(this);
			bF.timer = window.clearTimeout(bF.timer)
		} else {
			dq = this.bfH();
			bF = {
				request: dq,
				bind: [this]
			};
			this.HX(this.gq, bF);
			bo.bW(dq, "load", this.HV.bq(this));
			bo.bW(dq, "error", this.lu.bq(this, {
				code: dJ.AF,
				message: "无法加载指定资源文件[" + this.gq + "]！"
			}))
		}
		if (this.vx != 0) bF.timer = window.setTimeout(this.lu.bq(this, {
			code: dJ.Du,
			message: "指定资源文件[" + this.gq + "]载入超时！"
		}), this.vx || Ro);
		if (!!dq) this.vR(dq);
		this.bJ("onloading");
		return this
	};
	fA.HQ = function(bn) {
		if (!bn || !bn.length) {
			this.bJ("onerror", {
				code: dJ.zP,
				message: "请指定要载入的资源队列！"
			});
			return this
		}
		this.ju = bm.ml();
		var bF = {
			error: 0,
			loaded: 0,
			total: bn.length
		};
		this.HX(this.ju, bF);
		for (var i = 0, l = bn.length; i < l; i++) {
			if (!bn[i]) {
				bF.total--;
				continue
			}
			this.QV(bn[i])
		}
		this.bJ("onloading");
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = bh("nej.e"),
		bI = bh("nej.h"),
		bs = bh("nej.ut.j"),
		oK;
	if (!!bs.wx) return;
	bs.wx = NEJ.C();
	oK = bs.wx.cg(bs.oT);
	oK.bfH = function() {
		var dp = bg.hw("iframe");
		dp.width = 0;
		dp.height = 0;
		dp.style.display = "none";
		return dp
	};
	oK.vR = function(dq) {
		dq.src = this.gq;
		document.body.appendChild(dq)
	};
	oK.lu = function(eL) {
		var dp = (this.lw(this.gq) || bX).request;
		this.tZ("onerror", eL);
		bI.us(dp)
	};
	oK.HV = function() {
		var jk = null,
			dp = (this.lw(this.gq) || bX).request;
		try {
			jk = dp.contentWindow.document.body
		} catch (ex) {}
		this.tZ("onloaded", jk);
		bI.us(dp)
	}
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bs = bh("nej.ut.j"),
		wD;
	if (!!bs.ts) return;
	bs.ts = NEJ.C();
	wD = bs.ts.cg(bs.oT);
	wD.bfH = function() {
		return bg.hw("link")
	};
	wD.vR = function(dq) {
		dq.href = this.gq;
		document.head.appendChild(dq)
	}
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bs = bh("nej.ut.j"),
		tf;
	if (!!bs.sE) return;
	bs.sE = NEJ.C();
	tf = bs.sE.cg(bs.oT);
	tf.cP = function(bf) {
		this.cS(bf);
		this.HO = bf.async;
		this.wS = bf.charset;
		this.oS.async = !1;
		this.oS.charset = this.wS
	};
	tf.bfH = function() {
		var dq = bg.hw("script");
		if (this.HO != null) dq.async = !!this.HO;
		if (this.wS != null) dq.charset = this.wS;
		return dq
	};
	tf.HW = function(dq) {
		bg.gC(dq)
	}
})();
(function() {
	var bh = NEJ.P,
		bE = bh("nej.j"),
		bs = bh("nej.ut.j");
	bE.YZ = function(bC, bf) {
		bs.sE.bZ(bf).tK(bC);
		return this
	};
	bE.QF = function(bn, bf) {
		bs.sE.bZ(bf).HQ(bn);
		return this
	};
	bE.Ze = function(bC, bf) {
		bs.ts.bZ(bf).tK(bC);
		return this
	};
	bE.QD = function(bn, bf) {
		bs.ts.bZ(bf).HQ(bn);
		return this
	};
	bE.HM = function(bC, bf) {
		bs.wx.bZ(bf).tK(bC);
		return this
	}
})();
(function() {
	var o = NEJ.O,
		u = NEJ.P("nej.u"),
		j = NEJ.P("nej.j");
	j.mr = function() {
		var lK = new Date,
			Qv = +lK,
			Qu = 864e5;
		var Qs = function(bB) {
			var gM = document.cookie,
				kl = "\\b" + bB + "=",
				rX = gM.search(kl);
			if (rX < 0) return "";
			rX += kl.length - 2;
			var ox = gM.indexOf(";", rX);
			if (ox < 0) ox = gM.length;
			return gM.substring(rX, ox) || ""
		};
		return function(bB, bk) {
			if (bk === undefined) return Qs(bB);
			if (u.dW(bk)) {
				if (!!bk) {
					document.cookie = bB + "=" + bk + ";";
					return bk
				}
				bk = {
					expires: -100
				}
			}
			bk = bk || o;
			var gM = bB + "=" + (bk.value || "") + ";";
			delete bk.value;
			if (bk.expires !== undefined) {
				lK.setTime(Qv + bk.expires * Qu);
				bk.expires = lK.toGMTString()
			}
			gM += u.IM(bk, ";");
			document.cookie = gM
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		gn = bh("nej.c"),
		dJ = bh("nej.g"),
		bg = bh("nej.e"),
		bE = bh("nej.j"),
		bR = bh("nej.ut"),
		bs = bh("nej.ut.j"),
		bm = bh("nej.u"),
		bj;
	if (!!bs.oy) return;
	bs.oy = NEJ.C();
	bj = bs.oy.cg(bR.dE);
	bj.cP = function(bf) {
		this.cS(bf);
		this.iE = {
			noescape: false,
			url: "",
			sync: !1,
			cookie: !1,
			type: "text",
			method: "GET",
			timeout: 6e4
		};
		NEJ.EX(this.iE, bf);
		var kH = gn.bL("csrf");
		if (!!kH.cookie && !!kH.param) {
			var cn = encodeURIComponent(kH.param) + "=" + encodeURIComponent(bE.mr(kH.cookie) || ""),
				qN = this.iE.url.indexOf("?") < 0 ? "?" : "&";
			this.iE.url += qN + cn
		}
		this.vk = bf.headers || {};
		var bU = this.vk[dJ.lS];
		if (bU == null) this.vk[dJ.lS] = dJ.Mh
	};
	bj.dx = function() {
		this.dA();
		delete this.kE;
		delete this.iE;
		delete this.vk
	};
	bj.Qr = function(bU) {
		var bQ = {
			r: /\<|\>/g,
			"<": "&lt;",
			">": "&gt;"
		};
		if (!this.iE.noescape) {
			return bm.rl(bQ, bU)
		} else {
			return bU
		}
	};
	bj.iO = function(bi) {
		var ey = bi.status;
		if (ey == -1) {
			this.bJ("onerror", {
				code: dJ.Du,
				message: "请求[" + this.iE.url + "]超时！"
			});
			return
		}
		if (("" + ey).indexOf("2") != 0) {
			this.bJ("onerror", {
				data: ey,
				code: dJ.AF,
				message: "服务器返回异常状态[" + ey + "]!"
			});
			return
		}
		this.bJ("onload", bg.Vu(this.Qr(bi.result), this.iE.type))
	};
	bj.dT = bY;
	bj.Qq = function(bk) {
		var bC = this.iE.url;
		if (!bC) {
			this.bJ("onerror", {
				code: dJ.zP,
				message: "没有输入请求地址！"
			});
			return this
		}
		try {
			this.iE.data = bk == null ? null : bk;
			var bi = {
				request: this.iE,
				headers: this.vk
			};
			try {
				this.bJ("onbeforerequest", bi)
			} catch (ex) {
				console.error(ex.message);
				console.error(ex.stack)
			}
			this.dT(bi)
		} catch (e) {
			this.bJ("onerror", {
				code: dJ.AF,
				message: "请求[" + bC + "]失败:" + e.message + "！"
			})
		}
		return this
	};
	bj.mL = bY
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		bI = bh("nej.h"),
		dJ = bh("nej.g"),
		bm = bh("nej.u"),
		bs = bh("nej.ut.j"),
		bF = {},
		oF;
	if (!!bs.tJ) return;
	bs.tJ = NEJ.C();
	oF = bs.tJ.cg(bs.oy);
	oF.dx = function() {
		this.dA();
		window.clearTimeout(this.gI);
		delete this.gI;
		try {
			this.hI.onreadystatechange = bY;
			this.hI.abort()
		} catch (e) {}
		delete this.hI
	};
	oF.dT = function() {
		var Qg = function(bA, bba) {
			this.hI.setRequestHeader(bba, bA)
		};
		return function(bf) {
			var dq = bf.request,
				oz = bf.headers;
			this.hI = bI.PZ();
			if (oz[dJ.lS] === dJ.nC) {
				delete oz[dJ.lS];
				this.hI.upload.onprogress = this.kA.bq(this, 1);
				if (dq.data.tagName === "FORM") dq.data = new FormData(dq.data)
			}
			this.hI.onreadystatechange = this.kA.bq(this, 2);
			if (dq.timeout != 0) {
				this.gI = window.setTimeout(this.kA.bq(this, 3), dq.timeout)
			}
			this.hI.open(dq.method, dq.url, !dq.sync);
			bm.dB(oz, Qg, this);
			if (!!this.iE.cookie && "withCredentials" in this.hI) this.hI.withCredentials = !0;
			this.hI.send(dq.data)
		}
	}();
	oF.kA = function(bv) {
		switch (bv) {
			case 1:
				this.bJ("onuploading", arguments[1]);
				break;
			case 2:
				if (this.hI.readyState == 4) this.iO({
					status: this.hI.status,
					result: this.hI.responseText || ""
				});
				break;
			case 3:
				this.iO({
					status: -1
				});
				break
		}
	};
	oF.mL = function() {
		this.iO({
			status: 0
		});
		return this
	}
})();
(function() {
	var dI = NEJ.P("nej.p"),
		bs = window,
		fm = dI.tm,
		HI = fm.ipad || fm.iphone;
	if (!HI && !!bs.requestAnimationFrame && !!bs.cancelRequestAnimationFrame) return;
	var fu = dI.eV.prefix.pro;
	if (!HI && !!bs[fu + "RequestAnimationFrame"] && !!bs[fu + "CancelRequestAnimationFrame"]) {
		bs.requestAnimationFrame = bs[fu + "RequestAnimationFrame"];
		bs.cancelRequestAnimationFrame = bs[fu + "CancelRequestAnimationFrame"];
		return
	}
	var PX = fm.desktop ? 80 : fm.ios ? 50 : 30;
	bs.requestAnimationFrame = function(en) {
		return window.setTimeout(function() {
			try {
				en(+(new Date))
			} catch (ex) {}
		}, 1e3 / PX)
	};
	bs.cancelRequestAnimationFrame = function(bt) {
		window.clearTimeout(bt);
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		bm = bh("nej.u"),
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bI = bh("nej.h"),
		cz = bh("nej.x"),
		rU = bh("nej.ut.j.cb"),
		ih;
	if (!!bg.yp) return;
	bg.yp = cz.yp = function() {
		var bF = {},
			dO = /^(?:mouse.*|(?:dbl)?click)$/i;
		window.onflashevent = function(bi) {
			var bt = decodeURIComponent(bi.target),
				bv = bi.type.toLowerCase();
			var cL = bF[bt + "-on" + bv];
			if (!!cL) {
				try {
					cL(bi)
				} catch (e) {}
				return
			}
			var cW = bF[bt + "-tgt"];
			if (!!cW && dO.test(bv)) {
				HF(cW, bi)
			}
		};
		var PP = function(bf) {
			var cl = bg.bL(bf.parent) || document.body,
				gp = bg.eH(ih, bf);
			cl.insertAdjacentHTML(!bf.hidden ? "beforeEnd" : "afterBegin", gp)
		};
		var HF = function(bt, bi) {
			var bv = bi.type.toLowerCase();
			requestAnimationFrame(function() {
				bo.bJ(bt, bv)
			})
		};
		var PO = function(ht) {
			return !!ht && !!ht.inited && !!ht.inited()
		};
		var HD = function(bt) {
			var bP = [document.embeds[bt], bg.bL(bt), document[bt], window[bt]],
				bfo = bm.dB(bP, PO),
				ht = bP[bfo],
				yA = bt + "-count";
			bF[yA]++;
			if (!!ht || bF[yA] > 100) {
				bF[bt](ht);
				delete bF[bt];
				delete bF[yA];
				return
			}
			window.setTimeout(HD.bq(null, bt), 300)
		};
		var PM = function(bf) {
			var bt = bf.id,
				eY = bf.params;
			if (!eY) {
				eY = {};
				bf.params = eY
			}
			var kx = eY.flashvars || "";
			kx += (!kx ? "" : "&") + ("id=" + bt);
			if (!bf.hidden && (!!bf.target || bI.As(eY.wmode))) {
				var HC = bg.gl(bf.target) || bg.gl(bf.parent),
					HB = bm.ID();
				rU["cb" + HB] = HF.bq(null, HC);
				kx += "&onevent=nej.ut.j.cb.cb" + HB;
				bF[bt + "-tgt"] = HC
			}
			eY.flashvars = kx;
			bm.dB(bf, function(bA, bba) {
				if (bm.es(bA) && bba != "onready") {
					bF[bt + "-" + bba] = bA
				}
			})
		};
		return function(bf) {
			bf = NEJ.X({}, bf);
			if (!bf.src) return;
			var bt = "flash_" + bm.ID();
			bf.id = bt;
			PM(bf);
			PP(bf);
			if (!bf.onready) return;
			bF[bt] = bf.onready;
			bF[bt + "-count"] = 0;
			HD(bt)
		}
	}();
	ih = bg.ky('{var hide  = defined("hidden")&&!!hidden}{var param = defined("params")&&params||NEJ.O}{var width = !hide?width:"1px",height = !hide?height:"1px"}{if hide}<div style="position:absolute;top:0;left:0;width:1px;height:1px;z-index:10000;overflow:hidden;">{/if}<object classid = "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"codebase = "http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab"width = "${width|default:"100px"}"height = "${height|default:"100px"}" id="${id}"><param value="${src}" name="movie">{for x in param}<param value="${x}" name="${x_key}"/>{/for}<embed src="${src}" name="${id}"width="${width|default:"100px"}"height="${height|default:"100px"}"pluginspage="http://www.adobe.com/go/getflashplayer"type="application/x-shockwave-flash"{for x in param}${x_key}="${x}" {/for}></embed></object>{if hide}</div>{/if}');
	cz.isChange = !0
})();
(function() {
	var bh = NEJ.P,
		gn = bh("nej.c"),
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bs = bh("nej.ut.j"),
		rU = bh("nej.ut.j.cb"),
		bF = {},
		dn = +(new Date),
		yO;
	if (!!bs.yR) return;
	rU["ld" + dn] = function(bba, dR) {
		var eB = bF[bba];
		if (!eB) return;
		delete bF[bba];
		eB.iO({
			status: 200,
			result: dR
		})
	};
	rU["er" + dn] = function(bba, ey) {
		var eB = bF[bba];
		if (!eB) return;
		delete bF[bba];
		eB.iO({
			status: ey || 0
		})
	};
	bs.yR = NEJ.C();
	yO = bs.yR.cg(bs.oy);
	yO.dT = function(bf) {
		var ht = bF.flash;
		if (bm.eZ(ht)) {
			ht.push(this.dT.bq(this, bf));
			return
		}
		if (!ht) {
			bF.flash = [this.dT.bq(this, bf)];
			bg.yp({
				hidden: !0,
				src: gn.bL("ajax.swf"),
				onready: function(ht) {
					if (!ht) return;
					var bn = bF.flash;
					bF.flash = ht;
					bm.gU(bn, function(cL) {
						try {
							cL()
						} catch (e) {}
					})
				}
			});
			return
		}
		this.kE = "swf-" + bm.ml();
		bF[this.kE] = this;
		var bk = NEJ.EX({
			url: "",
			data: null,
			method: "GET"
		}, bf.request);
		bk.key = this.kE;
		bk.headers = bf.headers;
		bk.onerror = "nej.ut.j.cb.er" + dn;
		bk.onloaded = "nej.ut.j.cb.ld" + dn;
		var Hy = gn.Rx(bk.url);
		if (!!Hy) bk.policyURL = Hy;
		ht.request(bk)
	};
	yO.mL = function() {
		delete bF[this.kE];
		this.iO({
			status: 0
		});
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bI = bh("nej.h");
	bI.Pz = function() {
		var cA = /^([\w]+?:\/\/.*?(?=\/|$))/i;
		return function(fD) {
			fD = fD || "";
			if (cA.test(fD)) return RegExp.$1;
			return "*"
		}
	}();
	bI.Py = function(bk) {
		return bk
	};
	bI.Px = function(Hw, bf) {
		if (!Hw.postMessage) return;
		bf = bf || bX;
		Hw.postMessage(bI.Py(bf.data), bI.Pz(bf.origin))
	}
})();
(function() {
	var bh = NEJ.P,
		bI = bh("nej.h"),
		bg = bh("nej.e"),
		bE = bh("nej.j");
	bE.Pu = function() {
		var hy = window.name || "_parent",
			Ps = {
				jP: window.top,
				hy: window,
				cl: window.parent
			};
		return function(cW, bf) {
			if (typeof cW == "string") {
				cW = Ps[cW] || window.frames[cW];
				if (!cW) return this
			}
			var bk = NEJ.X({
				origin: "*",
				source: hy
			}, bf);
			bI.Px(cW, bk);
			return this
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		gn = bh("nej.c"),
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bE = bh("nej.j"),
		bs = bh("nej.ut.j"),
		bF = {},
		sP;
	if (!!bs.zq) return;
	bs.zq = NEJ.C();
	sP = bs.zq.cg(bs.oy);
	sP.cI = function() {
		var gW = "NEJ-AJAX-DATA:",
			mG = !1;
		var zv = function(bi) {
			var bk = bi.data;
			if (bk.indexOf(gW) != 0) return;
			bk = JSON.parse(bk.replace(gW, ""));
			var eB = bF[bk.key];
			if (!eB) return;
			delete bF[bk.key];
			bk.result = decodeURIComponent(bk.result || "");
			eB.iO(bk)
		};
		var zz = function() {
			if (!mG) {
				mG = !0;
				bo.bW(window, "message", zv)
			}
		};
		return function() {
			this.dv();
			zz()
		}
	}();
	sP.dT = function(bf) {
		var dq = bf.request,
			eB = gn.Rj(dq.url),
			lX = bF[eB];
		if (bm.eZ(lX)) {
			lX.push(this.dT.bq(this, bf));
			return
		}
		if (!lX) {
			bF[eB] = [this.dT.bq(this, bf)];
			bg.xO({
				src: eB,
				visible: !1,
				onload: function(bi) {
					var bn = bF[eB];
					bF[eB] = bo.cK(bi).contentWindow;
					bm.gU(bn, function(cL) {
						try {
							cL()
						} catch (e) {}
					})
				}
			});
			return
		}
		this.kE = "frm-" + bm.ml();
		bF[this.kE] = this;
		var bk = NEJ.EX({
			url: "",
			data: null,
			timeout: 0,
			method: "GET"
		}, dq);
		bk.key = this.kE;
		bk.headers = bf.headers;
		bE.Pu(bF[eB], {
			data: bk
		})
	};
	sP.mL = function() {
		delete bF[this.kE];
		this.iO({
			status: 0
		});
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		dJ = bh("nej.g"),
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bE = bh("nej.j"),
		bs = bh("nej.ut.j"),
		bF = {},
		nW;
	if (!!bs.zE) return;
	bs.zE = NEJ.C();
	nW = bs.zE.cg(bs.oy);
	nW.cI = function() {
		var gW = "NEJ-UPLOAD-RESULT:",
			mG = !1;
		var zv = function(bi) {
			var bk = bi.data;
			if (bk.indexOf(gW) != 0) return;
			bk = JSON.parse(bk.replace(gW, ""));
			var eB = bF[bk.key];
			if (!eB) return;
			delete bF[bk.key];
			eB.iO(decodeURIComponent(bk.result))
		};
		var zz = function() {
			if (!mG) {
				mG = !0;
				bo.bW(window, "message", zv)
			}
		};
		return function() {
			this.dv();
			zz()
		}
	}();
	nW.dx = function() {
		this.dA();
		bg.gC(this.zF);
		delete this.zF;
		window.clearTimeout(this.gI);
		delete this.gI
	};
	nW.iO = function(dR) {
		var bN;
		try {
			bN = JSON.parse(dR);
			this.bJ("onload", bN)
		} catch (e) {
			this.bJ("onerror", {
				code: dJ.Ye,
				message: dR
			})
		}
	};
	nW.dT = function() {
		var Pr = function() {
			var jk, dR;
			try {
				var jk = this.zF.contentWindow.document.body,
					dR = jk.innerText || jk.textContent
			} catch (e) {
				return
			}
			this.iO(dR)
		};
		var zI = function(bC, dS, gM) {
			bE.cN(bC, {
				type: "json",
				method: "POST",
				cookie: gM,
				mode: parseInt(dS) || 0,
				onload: function(bk) {
					if (!this.gI) return;
					this.bJ("onuploading", bk);
					this.gI = window.setTimeout(zI.bq(this, bC, dS, gM), 1e3)
				}.bq(this),
				onerror: function(eL) {
					if (!this.gI) return;
					this.gI = window.setTimeout(zI.bq(this, bC, dS, gM), 1e3)
				}.bq(this)
			})
		};
		return function(bf) {
			var dq = bf.request,
				oz = bf.headers,
				fG = dq.data,
				bB = "fom-" + bm.ml();
			bF[bB] = this;
			fG.target = bB;
			fG.method = "POST";
			fG.enctype = dJ.nC;
			fG.encoding = dJ.nC;
			var bC = fG.action || "",
				mj = bC.indexOf("?") <= 0 ? "?" : "&";
			fG.action = bC + mj + "_proxy_=form";
			this.zF = bg.xO({
				name: bB,
				onload: function(bi) {
					var lX = bo.cK(bi);
					bo.bW(lX, "load", Pr.bq(this));
					fG.submit();
					var Hu = (fG.nej_query || bX).value;
					if (!Hu) return;
					var dS = (fG.nej_mode || bX).value,
						gM = (fG.nej_cookie || bX).value == "true";
					this.gI = window.setTimeout(zI.bq(this, Hu, dS, gM), 100)
				}.bq(this)
			})
		}
	}();
	nW.mL = function() {
		this.bJ("onerror", {
			code: dJ.LK,
			message: "客户端终止文件上传"
		});
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bI = bh("nej.h"),
		bs = bh("nej.ut.j");
	bI.PZ = function() {
		return new XMLHttpRequest
	};
	bI.Po = function(dS, rN, bf) {
		var bQ = !!rN ? {
			2: bs.zE
		} : {
			2: bs.zq,
			3: bs.yR
		};
		return (bQ[dS] || bs.tJ).bZ(bf)
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bI = bh("nej.h"),
		dJ = bh("nej.g"),
		bm = bh("nej.u"),
		bE = bh("nej.j"),
		bs = bh("nej.ut.j"),
		pU = {},
		nO = bY;
	bE.mL = function(dm) {
		var bF = pU[dm];
		if (!bF) return this;
		bF.req.mL();
		return this
	};
	bE.Zn = function(ek) {
		nO = ek || bY;
		return this
	};
	bE.cN = function() {
		var eC = (location.protocol + "//" + location.host).toLowerCase();
		var Pl = function(bC) {
			var fD = bm.TI(bC);
			return !!fD && fD != eC
		};
		var Pk = function(oz) {
			return (oz || bX)[dJ.lS] == dJ.nC
		};
		var Ph = function(bf) {
			var rN = Pk(bf.headers);
			if (!Pl(bf.url) && !rN) return bs.tJ.bZ(bf);
			return bI.Po(bf.mode, rN, bf)
		};
		var mA = function(dm) {
			var bF = pU[dm];
			if (!bF) return;
			if (!!bF.req) bF.req.cF();
			delete pU[dm]
		};
		var Hs = function(dm, bv) {
			var bF = pU[dm];
			if (!bF) return;
			mA(dm);
			try {
				var bi = {
					type: bv,
					result: arguments[2]
				};
				nO(bi);
				if (!bi.stopped)(bF[bv] || bY)(bi.result)
			} catch (ex) {
				console.error(ex.message);
				console.error(ex)
			}
		};
		var qH = function(dm, bk) {
			Hs(dm, "onload", bk)
		};
		var Ai = function(dm, eL) {
			Hs(dm, "onerror", eL)
		};
		return function(bC, bf) {
			bf = bf || {};
			var dm = bm.ml(),
				bF = {
					onload: bf.onload || bY,
					onerror: bf.onerror || bY
				};
			pU[dm] = bF;
			bf.onload = qH.bq(null, dm);
			bf.onerror = Ai.bq(null, dm);
			if (!!bf.query) {
				var mj = bC.indexOf("?") < 0 ? "?" : "&",
					cn = bf.query;
				if (bm.jq(cn)) cn = bm.fr(cn);
				if (!!cn) bC += mj + cn
			}
			bf.url = bC;
			bF.req = Ph(bf);
			bF.req.Qq(bf.data);
			return dm
		}
	}();
	bE.Zo = function(fG, bf) {
		var lk = {
			mode: 0,
			type: "json",
			query: null,
			cookie: !1,
			headers: {},
			onload: null,
			onerror: null,
			onuploading: null,
			onbeforerequest: null
		};
		NEJ.EX(lk, bf);
		lk.data = fG;
		lk.method = "POST";
		lk.timeout = 0;
		lk.headers[dJ.lS] = dJ.nC;
		return bE.cN(fG.action, lk)
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bE = bh("nej.j"),
		bR = bh("nej.ut"),
		bF = {},
		qK = +(new Date) + "-";
	bg.eP = function() {
		var ef = 0;
		var qU = function() {
			if (ef > 0) return;
			ef = 0;
			bo.bJ(document, "templateready");
			bo.iW(document, "templateready")
		};
		var uv = function(dH, bf) {
			var di = bg.bH(dH, "src");
			if (!di) return;
			bf = bf || bX;
			var eg = bf.root;
			if (!eg) {
				eg = dH.ownerDocument.location.href
			} else {
				eg = bm.yH(eg)
			}
			di = di.split(",");
			bm.bfq(di, function(bA, bfo, bn) {
				bn[bfo] = bm.yH(bA, eg)
			});
			return di
		};
		var OY = function(dH, bf) {
			if (!dH) return;
			var di = uv(dH, bf);
			if (!!di) bE.QD(di, {
				version: bg.bH(dH, "version")
			});
			bg.Jw(dH.value)
		};
		var OX = function(bA) {
			ef--;
			bg.Jv(bA);
			qU()
		};
		var OQ = function(dH, bf) {
			if (!dH) return;
			var di = uv(dH, bf),
				jo = dH.value;
			if (!!di) {
				ef++;
				var bf = {
					version: bg.bH(dH, "version"),
					onloaded: OX.bq(null, jo)
				};
				window.setTimeout(bE.QF.bq(bE, di, bf), 0);
				return
			}
			bg.Jv(jo)
		};
		var OP = function(jk) {
			ef--;
			bg.eP(jk);
			qU()
		};
		var OO = function(dH, bf) {
			if (!dH) return;
			var di = uv(dH, bf)[0];
			if (!!di) {
				ef++;
				var bf = {
					version: bg.bH(dH, "version"),
					onloaded: OP
				};
				window.setTimeout(bE.HM.bq(bE, di, bf), 0)
			}
		};
		var ON = function(bt, dR) {
			ef--;
			bg.ux(bt, dR || "");
			qU()
		};
		var OM = function(dH, bf) {
			if (!dH || !dH.id) return;
			var bt = dH.id,
				di = uv(dH, bf)[0];
			if (!!di) {
				ef++;
				var bC = di + (di.indexOf("?") < 0 ? "?" : "&") + (bg.bH(dH, "version") || ""),
					bf = {
						type: "text",
						method: "GET",
						onload: ON.bq(null, bt)
					};
				window.setTimeout(bE.cN.bq(bE, bC, bf), 0)
			}
		};
		var OL = function(bl, bf) {
			var bv = bl.name.toLowerCase();
			switch (bv) {
				case "jst":
					bg.ky(bl, !0);
					return;
				case "txt":
					bg.ux(bl.id, bl.value || "");
					return;
				case "ntp":
					bg.lz(bl.value || "", bl.id);
					return;
				case "js":
					OQ(bl, bf);
					return;
				case "css":
					OY(bl, bf);
					return;
				case "html":
					OO(bl, bf);
					return;
				case "res":
					OM(bl, bf);
					return
			}
		};
		bR.hk.bZ({
			element: document,
			event: "templateready",
			oneventadd: qU
		});
		return function(bp, bf) {
			bp = bg.bL(bp);
			if (!!bp) {
				var bn = bp.tagName == "TEXTAREA" ? [bp] : bp.getElementsByTagName("textarea");
				bm.bfq(bn, function(bl) {
					OL(bl, bf)
				});
				bg.gC(bp, !0)
			}
			qU();
			return this
		}
	}();
	bg.ux = function(bba, bA) {
		bF[bba] = bA || "";
		return this
	};
	bg.nU = function(bba) {
		return bF[bba] || ""
	};
	bg.lz = function(bp, bba) {
		bba = bba || bm.ml();
		bp = bg.bL(bp) || bp;
		bg.ux(qK + bba, bp);
		bg.iI(bp);
		return bba
	};
	bg.mO = function(bba) {
		if (!bba) return null;
		bba = qK + bba;
		var bA = bg.nU(bba);
		if (!bA) return null;
		if (bm.dW(bA)) {
			bA = bg.nt(bA);
			bg.ux(bba, bA)
		}
		return bA.cloneNode(!0)
	};
	bg.Hn = function() {
		var nO = function(bA, bba) {
			return bba == "offset" || bba == "limit"
		};
		return function(bn, bfp, bf) {
			var bP = [];
			if (!bn || !bn.length || !bfp) return bP;
			bf = bf || bX;
			var fy = bn.length,
				gP = parseInt(bf.offset) || 0,
				iq = Math.min(fy, gP + (parseInt(bf.limit) || fy)),
				fj = {
					total: bn.length,
					range: [gP, iq]
				};
			NEJ.X(fj, bf, nO);
			for (var i = gP, cq; i < iq; i++) {
				fj.index = i;
				fj.data = bn[i];
				cq = bfp.bZ(fj);
				var bt = cq.Bl();
				bF[bt] = cq;
				cq.cF = cq.cF.fR(function(bt, cq) {
					delete bF[bt];
					delete cq.cF
				}.bq(null, bt, cq));
				bP.push(cq)
			}
			return bP
		}
	}();
	bg.Hm = function(bt) {
		return bF[bt]
	};
	bg.Zp = function(cZ, bf) {
		bf = bf || bX;
		bg.eP(bf.tid || "template-box");
		bo.bW(document, "templateready", function() {
			cZ.bZ().bJ("onshow", bf)
		})
	};
	bh("dbg").dumpTC = function() {
		return bF
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bR = bh("nej.ut"),
		bs = bh("nej.ui"),
		bj;
	if (!!bs.jv) return;
	bs.jv = NEJ.C();
	bj = bs.jv.cg(bR.dE);
	bj.cI = function() {
		this.dv();
		bg.JE();
		this.gj();
		this.fq()
	};
	bj.cP = function(bf) {
		this.cS(bf);
		this.Os(bf.clazz);
		this.Op(bf.parent)
	};
	bj.dx = function() {
		this.dA();
		this.GY();
		delete this.ho;
		bg.iI(this.bG);
		bg.cr(this.bG, this.BL);
		delete this.BL
	};
	bj.gj = bY;
	bj.fq = function() {
		if (!this.iy) this.Om();
		this.bG = bg.mO(this.iy);
		if (!this.bG) this.bG = bg.hw("div", this.hB);
		bg.cu(this.bG, this.hB)
	};
	bj.Om = bY;
	bj.Os = function(cO) {
		this.BL = cO || "";
		bg.cu(this.bG, this.BL)
	};
	bj.Ol = function() {
		if (!this.hB) return;
		bg.cu(this.ho, this.hB + "-parent")
	};
	bj.GY = function() {
		if (!this.hB) return;
		bg.cr(this.ho, this.hB + "-parent")
	};
	bj.GW = function() {
		return this.bG
	};
	bj.Op = function(cl) {
		if (!this.bG) return this;
		this.GY();
		if (bm.es(cl)) {
			this.ho = cl(this.bG)
		} else {
			this.ho = bg.bL(cl);
			if (!!this.ho) this.ho.appendChild(this.bG)
		}
		this.Ol();
		return this
	};
	bj.cE = function() {
		if (!this.ho || !this.bG || this.bG.parentNode == this.ho) return this;
		this.ho.appendChild(this.bG);
		return this
	};
	bj.dk = function() {
		bg.iI(this.bG);
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bI = bh("nej.h"),
		bs = bh("nej.ui"),
		jt, GU;
	if (!!bs.qE) return;
	bs.qE = NEJ.C();
	jt = bs.qE.cg(bs.jv);
	GU = bs.qE.ff;
	jt.cP = function(bf) {
		this.cS(bf);
		this.nz("oncontentready", bf.oncontentready || this.NY.bq(this));
		this.NW = !!bf.nohack;
		this.NV = !!bf.destroyable;
		this.GT(bf.content)
	};
	jt.dx = function() {
		this.bJ("onbeforerecycle");
		this.dA();
		this.tx();
		this.GT("");
		bg.BF(this.bG, {
			top: "",
			left: ""
		})
	};
	jt.NY = bY;
	jt.nr = bY;
	jt.tx = function() {
		bg.iI(this.bG);
		if (!!this.jO) {
			this.jO = bI.To(this.bG);
			delete this.jO
		}
	};
	jt.GT = function(bU) {
		if (!this.bG || !this.qy || bU == null) return this;
		bU = bU || "";
		bm.dW(bU) ? this.qy.innerHTML = bU : this.qy.appendChild(bU);
		this.bJ("oncontentready", this.qy);
		return this
	};
	jt.li = function(cj) {
		var bA = cj.top;
		if (bA != null) {
			bA += "px";
			bg.co(this.bG, "top", bA);
			bg.co(this.jO, "top", bA)
		}
		var bA = cj.left;
		if (bA != null) {
			bA += "px";
			bg.co(this.bG, "left", bA);
			bg.co(this.jO, "left", bA)
		}
		return this
	};
	jt.cE = function() {
		bg.co(this.bG, "visibility", "hidden");
		GU.cE.apply(this, arguments);
		this.nr();
		bg.co(this.bG, "visibility", "");
		if (!this.NW) {
			this.jO = bI.jO(this.bG)
		}
		return this
	};
	jt.dk = function() {
		this.NV ? this.cF() : this.tx();
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		bm = bh("nej.u"),
		bg = bh("nej.e"),
		bs = bh("nej.ui"),
		lh;
	if (!!bs.sX) return;
	bs.sX = NEJ.C();
	lh = bs.sX.cg(bs.jv);
	lh.cP = function(bf) {
		this.qo();
		this.cS(this.NT(bf));
		this.eh.onbeforerecycle = this.cF.bq(this);
		this.jW = this.wg()
	};
	lh.dx = function() {
		this.bJ("onbeforerecycle");
		this.dA();
		delete this.eh;
		bg.iI(this.bG);
		var qg = this.jW;
		if (!!qg) {
			delete this.jW;
			qg.cF()
		}
	};
	lh.wg = bY;
	lh.NT = function(bf) {
		var bw = {};
		bm.dB(bf, function(bfp, bba) {
			this.eh.hasOwnProperty(bba) ? this.eh[bba] = bfp : bw[bba] = bfp
		}, this);
		return bw
	};
	lh.qo = function() {
		this.eh = {
			clazz: "",
			parent: null,
			content: this.bG,
			destroyable: !1,
			oncontentready: null,
			nohack: !1
		}
	};
	lh.cE = function() {
		if (!!this.jW) this.jW.cE();
		this.bJ("onaftershow");
		return this
	};
	lh.dk = function() {
		if (!!this.jW) this.jW.dk();
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		dJ = bh("nej.g"),
		bI = bh("nej.h"),
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		bs = bh("nej.ui"),
		qf, GR;
	if (!!bs.pV) return;
	var gy = bg.kN(".#<uispace>{position:fixed;_position:absolute;z-index:100;top:0;bottom:0;left:0;right:0;width:100%;height:100%;background-image:url(" + dJ.El + ");}");
	bs.pV = NEJ.C();
	qf = bs.pV.cg(bs.jv);
	GR = bs.pV.ff;
	qf.cP = function(bf) {
		this.cS(bf);
		var bU = bf.content || "&nbsp;";
		bm.dW(bU) ? this.bG.innerHTML = bU : this.bG.appendChild(bU)
	};
	qf.dx = function() {
		this.dA();
		this.bG.innerHTML = "&nbsp;"
	};
	qf.gj = function() {
		this.hB = gy
	};
	qf.cE = function() {
		bI.Tj(this.bG);
		GR.cE.apply(this, arguments);
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bs = bh("nej.ut"),
		bbq;
	if (!!bs.wq) return;
	bs.wq = NEJ.C();
	bbq = bs.wq.cg(bs.dE);
	bbq.cP = function(bf) {
		this.cS(bf);
		this.NL = !!bf.overflow;
		this.bG = bg.bL(bf.body);
		this.pR = bg.bL(bf.view) || bg.Kk(this.bG);
		this.GO = bg.bL(bf.mbar) || this.bG;
		this.rQ = parseInt(bf.direction) || 0;
		if (!!bf.isRelative) {
			this.bG.style.position = "relative";
			this.bcQ = true;
			this.bcP()
		}
		this.eO([
			[document, "mouseup", this.NH.bq(this)],
			[document, "mousemove", this.wz.bq(this)],
			[this.GO, "mousedown", this.wA.bq(this)]
		])
	};
	bbq.bcP = function() {
		if (!!this.bcQ) {
			this.bbJ = bg.kT(this.bG, this.pR);
			this.bbJ.x -= parseInt(bg.hP(this.bG, "left")) || 0;
			this.bbJ.y -= parseInt(bg.hP(this.bG, "top")) || 0
		}
	};
	bbq.dx = function() {
		this.dA();
		delete this.bG;
		delete this.GO;
		delete this.pR
	};
	bbq.GL = function() {
		return {
			x: Math.max(this.pR.clientWidth, this.pR.scrollWidth) - this.bG.offsetWidth,
			y: Math.max(this.pR.clientHeight, this.pR.scrollHeight) - this.bG.offsetHeight
		}
	};
	bbq.wA = function(bi) {
		bo.ep(bi);
		if (!!this.fB) return;
		this.fB = {
			x: bo.oQ(bi),
			y: bo.pZ(bi)
		};
		this.GK = this.GL();
		this.bJ("ondragstart", bi)
	};
	bbq.wz = function(bi) {
		if (!this.fB) return;
		var cj = {
			x: bo.oQ(bi),
			y: bo.pZ(bi)
		};
		var Nz = cj.x - this.fB.x,
			Ny = cj.y - this.fB.y,
			bA = {
				top: (parseInt(bg.hP(this.bG, "top")) || 0) + Ny,
				left: (parseInt(bg.hP(this.bG, "left")) || 0) + Nz
			};
		if (this.bcQ) {
			this.bcP();
			bA.top = bA.top + this.bbJ.y;
			bA.left = bA.left + this.bbJ.x
		}
		this.fB = cj;
		this.li(bA)
	};
	bbq.NH = function(bi) {
		if (!this.fB) return;
		delete this.GK;
		delete this.fB;
		this.bJ("ondragend", this.Nx())
	};
	bbq.li = function(bi) {
		if (!this.NL) {
			var GI = this.GK || this.GL();
			bi.top = Math.min(GI.y, Math.max(0, bi.top));
			bi.left = Math.min(GI.x, Math.max(0, bi.left))
		}
		var eR = this.bG.style;
		if (this.bcQ) {
			this.bcP();
			bi.top = bi.top - this.bbJ.y;
			bi.left = bi.left - this.bbJ.x
		}
		if (this.rQ == 0 || this.rQ == 2) eR.top = bi.top + "px";
		if (this.rQ == 0 || this.rQ == 1) eR.left = bi.left + "px";
		this.bJ("onchange", bi);
		return this
	};
	bbq.Nx = function() {
		return {
			left: parseInt(bg.hP(this.bG, "left")) || 0,
			top: parseInt(bg.hP(this.bG, "top")) || 0
		}
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = NEJ.P("nej.e"),
		bo = NEJ.P("nej.v"),
		bm = NEJ.P("nej.u"),
		bR = NEJ.P("nej.ut"),
		bs = NEJ.P("nej.ui"),
		gy, ih, bj, cf;
	if (!!bs.tY) return;
	bs.tY = NEJ.C();
	bj = bs.tY.cg(bs.qE);
	cf = bs.tY.ff;
	bj.cI = function() {
		this.wT = {};
		this.pq = {
			onchange: this.wz.bq(this)
		};
		this.dv()
	};
	bj.cP = function(bf) {
		this.cS(bf);
		this.Nr(bf.mask);
		this.Np(bf.align);
		this.No(bf.title);
		if (!bf.draggable) return;
		this.xf = bR.wq.bZ(this.pq)
	};
	bj.dx = function() {
		this.dA();
		delete this.hj;
		delete this.pn;
		if (!!this.kS) {
			this.kS.cF();
			delete this.kS
		}
		if (!!this.xf) {
			this.xf.cF();
			delete this.xf
		}
	};
	bj.gj = function() {
		this.hB = gy;
		this.iy = ih
	};
	bj.fq = function() {
		this.gD();
		var bn = bg.gL(this.bG);
		this.qy = bn[1];
		this.pq.mbar = bn[0];
		this.pq.body = this.bG;
		bo.bW(bn[2], "mousedown", this.Nm.bq(this));
		bo.bW(this.pq.mbar, "mousedown", this.wA.bq(this));
		this.GF = bg.gL(this.pq.mbar)[0]
	};
	bj.Nm = function(bi) {
		bo.ep(bi);
		this.bJ("onclose", bi);
		if (!bi.stopped) {
			this.dk()
		}
	};
	bj.wA = function(bi) {
		bo.bJ(document, "click")
	};
	bj.wz = function(bi) {
		if (!this.jO) return;
		bg.BF(this.jO, {
			top: bi.top + "px",
			left: bi.left + "px"
		})
	};
	bj.nr = function() {
		var lO = [function() {
				return 0
			}, function(iQ, cY, bba) {
				return Math.max(0, iQ[bba] + cY[bba] / 2)
			}, function(iQ, cY, bba) {
				return iQ[bba] + cY[bba]
			}],
			Ng = ["left", "top"];
		return function() {
			var bA = {},
				eR = this.bG.style,
				bM = bg.ko(),
				iQ = {
					left: bM.scrollLeft,
					top: bM.scrollTop
				},
				cY = {
					left: bM.clientWidth - this.bG.offsetWidth,
					top: bM.clientHeight - this.bG.offsetHeight
				};
			bm.bfq(Ng, function(bba, bfo) {
				var cL = lO[this.hj[bfo]];
				if (!cL) return;
				bA[bba] = cL(iQ, cY, bba)
			}, this);
			this.li(bA)
		}
	}();
	bj.Nf = function() {
		if (!this.kS) {
			if (!this.pn) return;
			this.wT.parent = this.ho;
			this.kS = this.pn.bZ(this.wT)
		}
		this.kS.cE()
	};
	bj.tx = function() {
		if (!!this.kS) this.kS.dk();
		cf.tx.apply(this, arguments)
	};
	bj.Nr = function(lW) {
		if (!!lW) {
			if (lW instanceof bs.pV) {
				this.kS = lW;
				return
			}
			if (bm.es(lW)) {
				this.pn = lW;
				return
			}
			this.pn = bs.pV;
			if (bm.dW(lW)) this.wT.clazz = lW;
			return
		}
		this.pn = null
	};
	bj.No = function(jl, gp) {
		if (!!this.GF) {
			var qM = !gp ? "innerText" : "innerHTML";
			this.GF[qM] = jl || "标题"
		}
		return this
	};
	bj.Np = function() {
		var cA = /\s+/,
			MZ = {
				left: 0,
				center: 1,
				right: 2,
				auto: 3
			},
			MY = {
				top: 0,
				middle: 1,
				bottom: 2,
				auto: 3
			};
		return function(gf) {
			this.hj = (gf || "").split(cA);
			var dN = MZ[this.hj[0]];
			if (dN == null) dN = 1;
			this.hj[0] = dN;
			var dN = MY[this.hj[1]];
			if (dN == null) dN = 1;
			this.hj[1] = dN;
			return this
		}
	}();
	bj.cE = function() {
		cf.cE.apply(this, arguments);
		this.Nf();
		return this
	};
	gy = bg.kN(".#<uispace>{position:absolute;z-index:1000;border:1px solid #aaa;background:#fff;}.#<uispace> .zbar{line-height:30px;background:#8098E7;border-bottom:1px solid #aaa;}.#<uispace> .zcnt{padding:10px 5px;}.#<uispace> .zttl{margin-right:20px;text-align:left;}.#<uispace> .zcls{position:absolute;top:5px;right:0;width:20px;height:20px;line-height:20px;cursor:pointer;}");
	ih = bg.lz('<div class="' + gy + '"><div class="zbar"><div class="zttl">标题</div></div><div class="zcnt"></div><span class="zcls" title="关闭窗体">×</span></div>')
})();
(function() {
	var bh = NEJ.P,
		bm = bh("nej.u"),
		bs = bh("nej.ui"),
		xy;
	if (!!bs.rI) return;
	bs.rI = NEJ.C();
	xy = bs.rI.cg(bs.sX);
	xy.wg = function() {
		return bs.tY.bZ(this.eh)
	};
	xy.qo = function() {
		bs.rI.ff.qo.apply(this, arguments);
		this.eh.mask = null;
		this.eh.title = "标题";
		this.eh.align = "";
		this.eh.draggable = !1;
		this.eh.onclose = null
	}
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		cQ = bh("nej.ui"),
		bV = bh("nm.l"),
		bj, cf;
	bV.iM = NEJ.C();
	bj = bV.iM.cg(cQ.rI);
	bj.cP = function(bf) {
		bf.clazz = "m-layer z-show " + (bf.clazz || "");
		bf.nohack = true;
		bf.draggable = true;
		this.cS(bf)
	};
	bj.oq = function(bl, cG) {
		if (!bl) return;
		bg.co(bl, "display", !cG ? "none" : "");
		bl.lastChild.innerText = cG || ""
	};
	bj.GC = function(so, iK, MS, MR) {
		var cZ = "js-lock";
		if (iK === undefined) return bg.eF(so, cZ);
		!iK ? bg.cr(so, cZ) : bg.cu(so, cZ);
		so.firstChild.innerText = !iK ? MS : MR
	};
	bV.iM.cE = function(bf) {
		bf = bf || {};
		if (bf.mask === undefined) bf.mask = "m-mask";
		if (bf.parent === undefined) bf.parent = document.body;
		if (bf.draggable === undefined) bf.draggable = true;
		!!this.eG && this.eG.cF();
		this.eG = this.bZ(bf);
		this.eG.cE(bf);
		return this.eG
	};
	bV.iM.dk = function() {
		!!this.eG && this.eG.cF()
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bD = bh("nm.d"),
		bz = bh("nm.x"),
		bV = bh("nm.l"),
		bj, cf;
	bV.xQ = NEJ.C();
	bj = bV.xQ.cg(bV.iM);
	cf = bV.xQ.ff;
	bj.cP = function(bf) {
		this.cS(bf);
		if (bf.bubble === undefined) bf.bubble = true;
		this.MQ = bf.bubble;
		this.ig = bf.message || ""
	};
	bj.gj = function() {
		this.iy = bg.lz('<div class="lyct f-cb f-tc"></div>')
	};
	bj.fq = function() {
		this.gD();
		bo.bW(this.bG, "click", this.lI.bq(this))
	};
	bj.lI = function(bi) {
		var bl = bo.cK(bi, "d:action");
		if (!bl) return;
		if (bl.href) bo.fw(bi);
		if (bg.bH(bl, "action") == "close") this.dk();
		if (this.MQ === !1) bo.pk(bi);
		this.bJ("onaction", bg.bH(bl, "action"))
	};
	bj.cE = function() {
		cf.cE.call(this);
		this.bG.innerHTML = this.ig
	};
	var dn = bg.ky('<div class="f-fs1" style="line-height:22px;">${message|default:""}</div><div class="lybtn">{list buttons as item}<a hidefocus="true" class="u-btn2 ${item.klass} {if item.style}${item.style}{else}u-btn2-w2{/if}" href="#" {if !!item.action}data-action="${item.action}"{/if}><i>${item.text}</i></a>{/list}</div>');
	bz.GB = function() {
		var bbh;
		var cq;
		var ML = function(GA, cC) {
			if (bm.es(GA, "function") && GA(cC) != -1) cq.cF()
		};
		var oE = function() {
			!!cq && cq.dk()
		};
		return function(bf) {
			clearTimeout(bbh);
			bf = bf || {};
			bf.title = bf.title || "提示";
			bf.clazz = bf.clazz || "";
			bf.parent = bf.parent || document.body;
			bf.buttons = bf.buttons || [];
			bf.message = bg.eH(dn, bf);
			bf.onaction = ML.bq(null, bf.action);
			if (bf.mask === undefined) bf.mask = true;
			if (bf.draggable === undefined) bf.draggable = true;
			!!cq && cq.cF();
			cq = bV.xQ.bZ(bf);
			cq.cE();
			if (bf.autoclose) bbh = setTimeout(oE.bq(null), 2e3);
			return cq
		}
	}();
	bz.yh = function(bf) {
		bf = bf || {};
		bf.clazz = bf.clazz || "m-layer-w1";
		bf.buttons = [{
			klass: "u-btn2-2",
			action: "close",
			text: bf.btntxt || "确定"
		}];
		var cq = bz.GB(bf);
		return cq
	};
	bz.MH = function(bf) {
		bf = bf || {};
		bf.clazz = bf.clazz || "m-layer-w2";
		bf.buttons = [{
			klass: "u-btn2-2",
			action: "ok",
			text: bf.btnok || "确定",
			style: bf.okstyle || ""
		}, {
			klass: "u-btn2-1",
			action: "close",
			text: bf.btncc || "取消",
			style: bf.ccstyle || ""
		}];
		var cq = bz.GB(bf);
		return cq
	}
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u");
	bg.MG = function() {
		var dO = /[\r\n]/gi,
			bF = {};
		var MF = function(hu) {
			return (hu || "").replace(dO, "aa").length
		};
		var Gz = function(bt) {
			var bu = bF[bt],
				hv = bg.bL(bt),
				hM = bg.bL(bt + "-counter");
			if (!hv || !bu) return;
			var bi = {
				input: hv.value
			};
			bi.length = bu.onlength(bi.input);
			bi.delta = bu.max - bi.length;
			bu.onchange(bi);
			hM.innerHTML = bi.value || "剩余" + bi.delta + "个字"
		};
		return function(bp, bf) {
			var bt = bg.gl(bp);
			if (!bt || !!bF[bt]) return;
			var bu = NEJ.X({}, bf);
			bu.onchange = bu.onchange || bY;
			bu.onlength = MF;
			if (!bu.max) {
				var Gy = parseInt(bg.fl(bt, "maxlength")),
					Gx = parseInt(bg.bH(bt, "maxLength"));
				bu.max = Gy || Gx || 100;
				if (!Gy && !!Gx) bu.onlength = bm.rA
			}
			bF[bt] = bu;
			bo.bW(bt, "input", Gz.bq(null, bt));
			var bl = bg.yr(bt, {
				nid: bu.nid || "js-counter",
				clazz: bu.clazz
			});
			bu.xid = bt + "-counter";
			bl.id = bu.xid;
			Gz(bt)
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bI = bh("nej.h");
	bI.Mz = function(bp, cO) {}
})();
(function() {
	var bh = NEJ.P,
		bI = bh("nej.h"),
		bg = bh("nej.e"),
		cz = bh("nej.x");
	bg.uD = cz.uD = function(bp, cO) {
		bI.Mz(bp, bg.bH(bp, "holder") || cO || "js-placeholder");
		return this
	};
	cz.isChange = !0
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bs = bh("nej.ut"),
		dL;
	if (!!bs.yz) return;
	bs.yz = NEJ.C();
	dL = bs.yz.cg(bs.dE);
	dL.cI = function() {
		this.dv();
		this.mE = {
			tp: {
				nid: "js-nej-tp"
			},
			ok: {
				nid: "js-nej-ok"
			},
			er: {
				nid: "js-nej-er"
			}
		}
	};
	dL.cP = function(bf) {
		this.cS(bf);
		this.gx = document.forms[bf.form] || bg.bL(bf.form);
		this.eO([
			[this.gx, "keypress", this.My.bq(this)]
		]);
		this.ig = bf.message || {};
		this.ig.pass = this.ig.pass || "&nbsp;";
		var dS = this.gS(this.gx, "focusMode", 1);
		if (!isNaN(dS)) {
			this.Gu = {
				mode: dS,
				clazz: bf.focus
			}
		}
		this.kK = bf.holder;
		this.mE.tp.clazz = "js-mhd " + (bf.tip || "js-tip");
		this.mE.ok.clazz = "js-mhd " + (bf.pass || "js-pass");
		this.mE.er.clazz = "js-mhd " + (bf.error || "js-error");
		this.Gs = bf.invalid || "js-invalid";
		this.Ms(bf);
		this.fX();
		if (!!this.yT) this.yT.focus()
	};
	dL.dx = function() {
		this.dA();
		delete this.ig;
		delete this.yT;
		delete this.mt;
		delete this.gx;
		delete this.Gq;
		delete this.oJ
	};
	dL.gS = function(bl, Mp, bv) {
		var bA = bg.bH(bl, Mp);
		switch (bv) {
			case 1:
				return parseInt(bA);
			case 2:
				return (bA || "").toLowerCase() == "true";
			case 3:
				return this.yZ(bA)
		}
		return bA
	};
	dL.kR = function(bA, bv) {
		if (bv == "date") return this.yZ(bA);
		return parseInt(bA)
	};
	dL.tk = function() {
		var fO = /^button|submit|reset|image|hidden|file$/i;
		return function(bl) {
			bl = this.bL(bl) || bl;
			var bv = bl.type;
			return !!bl.name && !fO.test(bl.type || "")
		}
	}();
	dL.Mo = function() {
		var fO = /^hidden$/i;
		return function(bl) {
			if (this.tk(bl)) return !0;
			bl = this.bL(bl) || bl;
			var bv = bl.type || "";
			return fO.test(bv)
		}
	}();
	dL.yZ = function() {
		var cA = /[-\/]/;
		var Mn = function(bA) {
			if (!bA) return "";
			bA = bA.split(cA);
			bA.push(bA.shift());
			return bA.join("/")
		};
		return function(bA) {
			if ((bA || "").toLowerCase() == "now") return +(new Date);
			return Date.parse(Mn(bA))
		}
	}();
	dL.My = function(bi) {
		if (bi.keyCode != 13) return;
		this.bJ("onenter", bi)
	};
	dL.Mk = function(bt, bB) {
		var oL = this.oJ[bB],
			bA = this.gS(bt, bB);
		if (!bA || !oL) return;
		this.tA(bt, oL);
		this.zo(bt, bB, bA)
	};
	dL.Mg = function(bt, bB) {
		try {
			var Gk = this.gS(bt, bB);
			if (!Gk) return;
			var bA = new RegExp(Gk);
			this.zo(bt, bB, bA);
			this.tA(bt, this.oJ[bB])
		} catch (e) {}
	};
	dL.LR = function(bt, bB) {
		var oL = this.oJ[bB];
		if (!!oL && this.gS(bt, bB, 2)) this.tA(bt, oL)
	};
	dL.zt = function(bt, bB, bA) {
		bA = parseInt(bA);
		if (isNaN(bA)) return;
		this.zo(bt, bB, bA);
		this.tA(bt, this.oJ[bB])
	};
	dL.FY = function(bt, bB) {
		this.zt(bt, bB, this.gS(bt, bB))
	};
	dL.FX = function(bt, bB) {
		this.zt(bt, bB, bg.fl(bt, bB))
	};
	dL.FW = function(bt, bB, bv) {
		var bA = this.kR(this.gS(bt, bB), this.gS(bt, "type"));
		this.zt(bt, bB, bA)
	};
	dL.LH = function() {
		var dO = /^input|textarea$/i;
		var sL = function(bi) {
			this.zC(bo.cK(bi))
		};
		var xH = function(bi) {
			var bl = bo.cK(bi);
			if (!this.gS(bl, "ignore", 2)) {
				this.FV(bl)
			}
		};
		return function(bl) {
			if (this.gS(bl, "autoFocus", 2)) this.yT = bl;
			var hg = bg.fl(bl, "placeholder");
			if (!!hg && hg != "null") bg.uD(bl, this.kK);
			if (!!this.Gu && dO.test(bl.tagName)) bg.JJ(bl, this.Gu);
			var bt = bg.gl(bl);
			this.LR(bt, "required");
			this.Mk(bt, "type");
			this.Mg(bt, "pattern");
			this.FX(bt, "maxlength");
			this.FX(bt, "minlength");
			this.FY(bt, "maxLength");
			this.FY(bt, "minLength");
			this.FW(bt, "min");
			this.FW(bt, "max");
			var bB = bl.name;
			this.ig[bB + "-tip"] = this.gS(bl, "tip");
			this.ig[bB + "-error"] = this.gS(bl, "message");
			this.zC(bl);
			var cT = this.mt[bt],
				bk = (cT || bX).data || bX,
				oO = this.gS(bl, "counter", 2);
			if (oO && (bk.maxlength || bk.maxLength)) {
				bg.MG(bt, {
					nid: this.mE.tp.nid,
					clazz: "js-counter"
				})
			}
			if (!!cT && dO.test(bl.tagName)) {
				this.eO([
					[bl, "focus", sL.bq(this)],
					[bl, "blur", xH.bq(this)]
				])
			} else if (this.gS(bl, "focus", 2)) {
				this.eO([
					[bl, "focus", sL.bq(this)]
				])
			}
		}
	}();
	dL.Ms = function() {
		var lM = {
			number: /^[\d]+$/i,
			url: /^[a-z]+:\/\/(?:[\w-]+\.)+[a-z]{2,6}.*$/i,
			email: /^[\w-\.]+@(?:[\w-]+\.)+[a-z]{2,6}$/i,
			date: function(v) {
				return !v || !isNaN(this.yZ(v))
			}
		};
		var LF = {
			required: function(bl) {
				var bv = bl.type,
					LD = !bl.value,
					LB = (bv == "checkbox" || bv == "radio") && !bl.checked;
				if (LB || LD) return -1
			},
			type: function(bl, bf) {
				var cA = this.Gq[bf.type],
					jo = bl.value.trim(),
					Ly = !!cA.test && !cA.test(jo),
					Lx = bm.es(cA) && !cA.call(this, jo);
				if (Ly || Lx) return -2
			},
			pattern: function(bl, bf) {
				if (!bf.pattern.test(bl.value)) return -3
			},
			maxlength: function(bl, bf) {
				if (bl.value.length > bf.maxlength) return -4
			},
			minlength: function(bl, bf) {
				if (bl.value.length < bf.minlength) return -5
			},
			maxLength: function(bl, bf) {
				if (bm.rA(bl.value) > bf.maxLength) return -4
			},
			minLength: function(bl, bf) {
				if (bm.rA(bl.value) < bf.minLength) return -5
			},
			min: function(bl, bf) {
				var dP = this.kR(bl.value, bf.type);
				if (isNaN(dP) || dP < bf.min) return -6
			},
			max: function(bl, bf) {
				var dP = this.kR(bl.value, bf.type);
				if (isNaN(dP) || dP > bf.max) return -7
			}
		};
		return function(bf) {
			this.Gq = NEJ.X(NEJ.X({}, lM), bf.type);
			this.oJ = NEJ.X(NEJ.X({}, LF), bf.attr)
		}
	}();
	dL.tA = function(bt, jM) {
		if (!bm.es(jM)) return;
		var cT = this.mt[bt];
		if (!cT || !cT.func) {
			cT = cT || {};
			cT.func = [];
			this.mt[bt] = cT
		}
		cT.func.push(jM)
	};
	dL.zo = function(bt, bB, bA) {
		if (!bB) return;
		var cT = this.mt[bt];
		if (!cT || !cT.data) {
			cT = cT || {};
			cT.data = {};
			this.mt[bt] = cT
		}
		cT.data[bB] = bA
	};
	dL.FV = function(bl) {
		bl = this.bL(bl) || bl;
		var cT = this.mt[bg.gl(bl)];
		if (!bl || !cT || !this.tk(bl)) return !0;
		var bw;
		bm.dB(cT.func, function(lO) {
			bw = lO.call(this, bl, cT.data);
			return bw != null
		}, this);
		if (bw == null) {
			var bi = {
				target: bl,
				form: this.gx
			};
			this.bJ("oncheck", bi);
			bw = bi.value
		}
		var bi = {
			target: bl,
			form: this.gx
		};
		if (bw != null) {
			bi.code = bw;
			this.bJ("oninvalid", bi);
			if (!bi.stopped) {
				this.Lw(bl, bi.value || this.ig[bl.name + bw])
			}
		} else {
			this.bJ("onvalid", bi);
			if (!bi.stopped) this.Lv(bl, bi.value)
		}
		return bw == null
	};
	dL.jK = function() {
		var Lu = function(un, tz) {
			return un == tz ? "block" : "none"
		};
		var Lm = function(bl, bv, cG) {
			var hg = FO.call(this, bl, bv);
			if (!hg && !!cG) hg = bg.yr(bl, this.mE[bv]);
			return hg
		};
		var FO = function(bl, bv) {
			var hg;
			if (bv == "tp") hg = bg.bL(bl.name + "-tip");
			if (!hg) hg = bg.cw(bl.parentNode, this.mE[bv].nid)[0];
			return hg
		};
		return function(bl, cG, bv) {
			bl = this.bL(bl) || bl;
			if (!bl) return;
			bv == "er" ? bg.cu(bl, this.Gs) : bg.cr(bl, this.Gs);
			var hg = Lm.call(this, bl, bv, cG);
			if (!!hg && !!cG) hg.innerHTML = cG;
			bm.dB(this.mE, function(bA, bba) {
				bg.co(FO.call(this, bl, bba), "display", Lu(bv, bba))
			}, this)
		}
	}();
	dL.zC = function(bl, cG) {
		this.jK(bl, cG || this.ig[bl.name + "-tip"], "tp");
		return this
	};
	dL.Lv = function(bl, cG) {
		this.jK(bl, cG || this.ig[bl.name + "-pass"] || this.ig.pass, "ok");
		return this
	};
	dL.Lw = function(bl, cG) {
		this.jK(bl, cG || this.ig[bl.name + "-error"], "er");
		return this
	};
	dL.Zw = function() {
		var dO = /^(?:radio|checkbox)$/i;
		var Lh = function(bA) {
			return bA == null ? "" : bA
		};
		var FI = function(bA, bl) {
			if (dO.test(bl.type || "")) {
				bl.checked = bA == bl.value
			} else {
				bl.value = Lh(bA)
			}
		};
		return function(bB, bA) {
			var bl = this.bL(bB);
			if (!bl) return this;
			if (bl.tagName == "SELECT" || !bl.length) {
				FI(bA, bl)
			} else {
				bm.bfq(bl, FI.bq(null, bA))
			}
			return this
		}
	}();
	dL.bL = function(bB) {
		return this.gx.elements[bB]
	};
	dL.Zx = function() {
		return this.gx
	};
	dL.KQ = function() {
		var dO = /^radio|checkbox$/i,
			fO = /^number|date$/;
		var KN = function(bQ, bl) {
			var bB = bl.name,
				bA = bl.value,
				cT = bQ[bB],
				bv = this.gS(bl, "type");
			if (fO.test(bv)) bA = this.kR(bA, bv);
			if (dO.test(bl.type) && !bl.checked) {
				bA = this.gS(bl, "value");
				if (!bA) return
			}
			if (!!cT) {
				if (!bm.eZ(cT)) {
					cT = [cT];
					bQ[bB] = cT
				}
				cT.push(bA)
			} else {
				bQ[bB] = bA
			}
		};
		return function() {
			var bw = {};
			bm.bfq(this.gx.elements, function(bl) {
				if (this.Mo(bl)) KN.call(this, bw, bl)
			}, this);
			return bw
		}
	}();
	dL.FA = function() {
		var KK = function(bl) {
			if (this.tk(bl)) this.zC(bl)
		};
		return function() {
			this.gx.reset();
			bm.bfq(this.gx.elements, KK, this);
			return this
		}
	}();
	dL.Zy = function() {
		this.gx.submit();
		return this
	};
	dL.fX = function() {
		var XU = function(bl) {
			if (this.tk(bl)) this.LH(bl)
		};
		return function() {
			this.mt = {};
			bm.bfq(this.gx.elements, XU, this);
			return this
		}
	}();
	dL.XH = function(bl) {
		bl = this.bL(bl) || bl;
		if (!!bl) return this.FV(bl);
		var bw = !0;
		bm.bfq(this.gx.elements, function(bl) {
			var XE = this.XH(bl);
			bw = bw && XE
		}, this);
		return bw
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bR = bh("nej.ut"),
		bm = bh("nej.u"),
		bz = bh("nm.x"),
		bV = bh("nm.l"),
		bj, cf;
	bV.Au = NEJ.C();
	bj = bV.Au.cg(bV.iM);
	cf = bV.Au.ff;
	bj.fq = function() {
		this.gD();
		bo.bW(this.bG, "click", this.lI.bq(this));
		bo.bW(document, "mousewheel", this.Ft.bq(this));
		if (!!document.body.addEventListener) document.body.addEventListener("DOMMouseScroll", this.Ft.bq(this))
	};
	bj.cP = function(bf) {
		this.cS(bf);
		if (bf.jst) {
			this.bG.innerHTML = bg.eH(bf.jst, bf.data)
		} else if (bf.ntp) {
			this.bG.appendChild(bg.mO(bf.ntp))
		} else if (bf.txt) {
			this.bG.innerHTML = bg.nU(bf.txt)
		} else if (bf.html) {
			this.bG.innerHTML = bf.html
		}
		var Fs = this.bG.getElementsByTagName("form");
		if (Fs.length) {
			this.gx = bR.yz.bZ({
				form: Fs[0]
			})
		}
		this.Ax = bg.gL(this.bG)[0]
	};
	bj.dx = function() {
		this.bJ("ondestroy");
		this.dA();
		this.bG.innerHTML = "";
		delete this.Ax
	};
	bj.lI = function(bi) {
		var bl = bo.cK(bi, "d:action"),
			bk = this.gx ? this.gx.KQ() : null,
			bi = {
				action: bg.bH(bl, "action")
			};
		if (bk) bi.data = bk;
		if (bi.action) {
			this.bJ("onaction", bi);
			if (bi.stopped) return;
			this.dk()
		}
	};
	bj.Ft = function(bi) {
		if (!this.Ax) return;
		bo.ep(bi);
		var cY = bi.wheelDelta || -bi.detail;
		this.Ax.scrollTop -= cY
	};
	bz.Fr = function(bf) {
		bf.destroyable = bf.destroyable || true;
		bf.title = bf.title || "提示";
		bf.draggable = true;
		bf.parent = document.body;
		bf.mask = bf.mask || true;
		var qg = bV.Au.bZ(bf);
		qg.cE();
		return qg
	}
})();
(function() {
	var p = NEJ.P("nej.u");
	var Fq = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",
		pg = {},
		mH = {};
	for (var i = 0, l = Fq.length, c; i < l; i++) {
		c = Fq.charAt(i);
		pg[i] = c;
		mH[c] = i
	}
	var WX = function(gw) {
		var bfo = 0,
			c, bw = [];
		while (bfo < gw.length) {
			c = gw[bfo];
			if (c < 128) {
				bw.push(String.fromCharCode(c));
				bfo++
			} else if (c > 191 && c < 224) {
				bw.push(String.fromCharCode((c & 31) << 6 | gw[bfo + 1] & 63));
				bfo += 2
			} else {
				bw.push(String.fromCharCode((c & 15) << 12 | (gw[bfo + 1] & 63) << 6 | gw[bfo + 2] & 63));
				bfo += 3
			}
		}
		return bw.join("")
	};
	var WW = function() {
		var gh = /\r\n/g;
		return function(bk) {
			bk = bk.replace(gh, "\n");
			var bw = [],
				hq = String.fromCharCode(237);
			if (hq.charCodeAt(0) < 0)
				for (var i = 0, l = bk.length, c; i < l; i++) {
					c = bk.charCodeAt(i);
					c > 0 ? bw.push(c) : bw.push(256 + c >> 6 | 192, 256 + c & 63 | 128)
				} else
					for (var i = 0, l = bk.length, c; i < l; i++) {
						c = bk.charCodeAt(i);
						if (c < 128) bw.push(c);
						else if (c > 127 && c < 2048) bw.push(c >> 6 | 192, c & 63 | 128);
						else bw.push(c >> 12 | 224, c >> 6 & 63 | 128, c & 63 | 128)
					}
			return bw
		}
	}();
	var WO = function(gw) {
		var bfo = 0,
			bw = [],
			dS = gw.length % 3;
		if (dS == 1) gw.push(0, 0);
		if (dS == 2) gw.push(0);
		while (bfo < gw.length) {
			bw.push(pg[gw[bfo] >> 2], pg[(gw[bfo] & 3) << 4 | gw[bfo + 1] >> 4], pg[(gw[bfo + 1] & 15) << 2 | gw[bfo + 2] >> 6], pg[gw[bfo + 2] & 63]);
			bfo += 3
		}
		if (dS == 1) bw[bw.length - 1] = bw[bw.length - 2] = "=";
		if (dS == 2) bw[bw.length - 1] = "=";
		return bw.join("")
	};
	var WE = function() {
		var qr = /\n|\r|=/g;
		return function(bk) {
			var bfo = 0,
				bw = [];
			bk = bk.replace(qr, "");
			for (var i = 0, l = bk.length; i < l; i += 4) bw.push(mH[bk.charAt(i)] << 2 | mH[bk.charAt(i + 1)] >> 4, (mH[bk.charAt(i + 1)] & 15) << 4 | mH[bk.charAt(i + 2)] >> 2, (mH[bk.charAt(i + 2)] & 3) << 6 | mH[bk.charAt(i + 3)]);
			var cm = bw.length,
				dS = bk.length % 4;
			if (dS == 2) bw = bw.slice(0, cm - 2);
			if (dS == 3) bw = bw.slice(0, cm - 1);
			return bw
		}
	}();
	p.Zz = function(bk) {
		return WX(WE(bk))
	};
	p.Wy = function(bk) {
		try {
			return window.btoa(bk)
		} catch (ex) {
			return WO(WW(bk))
		}
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bo = bh("nej.v"),
		bg = bh("nej.e"),
		bE = bh("nej.j"),
		bs = bh("nej.p"),
		bm = bh("nej.u"),
		bV = bh("nm.l"),
		dK = bh("nm.w"),
		bK = bh("nej.ui"),
		bD = bh("nm.d"),
		bz = bh("nm.x"),
		bj, cf;
	var TYPE_MAP = {
		13: "playlist",
		17: "program",
		18: "song",
		19: "album"
	};
	bV.Fn = NEJ.C();
	bj = bV.Fn.cg(bV.iM);
	bj.gj = function() {
		this.iy = "m-download-layer"
	};
	bj.fq = function() {
		this.gD();
		var bn = bg.cw(this.bG, "j-flag");
		this.AZ = bn[0];
		this.Bf = bn[1];
		this.Fm = bn[2];
		bE.cN("/client/version/get", {
			type: "json",
			onload: this.Wk.bq(this)
		});
		if (bs.tm.mac) {
			bg.cr(this.AZ.parentNode, "f-hide");
			bg.cu(this.Bf.parentNode, "f-hide");
			bg.cu(this.Fm, "f-hide")
		} else {
			bg.cu(this.AZ.parentNode, "f-hide");
			bg.cr(this.Bf.parentNode, "f-hide");
			bg.cr(this.Fm, "f-hide")
		}
	};
	bj.cP = function(bf) {
		bf.clazz = " m-layer-down";
		bf.parent = bf.parent || document.body;
		bf.title = "下载";
		bf.draggable = !0;
		bf.destroyalbe = !0;
		bf.mask = true;
		this.cS(bf);
		this.eO([
			[this.bG, "click", this.gN.bq(this)]
		]);
		this.jL = TYPE_MAP[bf.type];
		this.gv = bf.id
	};
	bj.dx = function() {
		this.dA()
	};
	bj.Bm = function() {
		this.dk()
	};
	bj.Fj = function(bi) {
		this.bJ("onok", bA);
		this.dk()
	};
	bj.gN = function(bi) {
		var bl = bo.cK(bi, "d:action");
		switch (bg.bH(bl, "action")) {
			case "download":
				this.dk();
				window.open(bg.bH(bl, "src"));
				break;
			case "orpheus":
				this.dk();
				location.href = "orpheus://" + bm.Wy(JSON.stringify({
					type: this.jL,
					id: this.gv,
					cmd: "download"
				}));
				break
		}
	};
	bj.Wk = function(bi) {
		if (bi.code == 200) {
			this.oo = bi.data;
			this.AZ.innerText = "V" + this.oo.mac;
			this.Bf.innerText = "V" + this.oo.pc
		}
	};
	bz.Fi = function(bf) {
		bV.Fn.bZ(bf).cE()
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bm = bh("nej.u"),
		bD = bh("nm.d"),
		ci = {};
	bD.bL = function(bba) {
		return ci[bba]
	};
	bD.tl = function(bba, bu) {
		ci[bba] = bu
	};
	bD.hm = function(bk) {
		bm.dB(bk, function(bfp, bba) {
			var bu = ci[bba] || {};
			NEJ.X(bu, bfp);
			ci[bba] = bu
		})
	}
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		bI = bh("nej.h");
	bI.Ff = function(bba) {
		return localStorage.getItem(bba)
	};
	bI.EY = function(bba, bA) {
		localStorage.setItem(bba, bA)
	};
	bI.Ve = function(bba) {
		localStorage.removeItem(bba)
	};
	bI.UO = function() {
		localStorage.clear()
	};
	bI.Uv = function() {
		var bw = [];
		for (var i = 0, l = localStorage.length; i < l; i++) bw.push(localStorage.key(i));
		return bw
	};
	bI.Us = function() {
		(document.onstorageready || bY)()
	};
	bI.Ur = function() {
		return !0
	}
})();
(function() {
	var bh = NEJ.P,
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		bI = bh("nej.h"),
		bE = bh("nej.j"),
		bR = bh("nej.ut"),
		bF = {};
	bE.po = function(bba, bA) {
		var EQ = JSON.stringify(bA);
		try {
			bI.EY(bba, EQ)
		} catch (ex) {
			console.error(ex.message);
			console.error(ex)
		}
		if (EQ != bI.Ff(bba)) bF[bba] = bA;
		return this
	};
	bE.lV = function(bba) {
		var bk = JSON.parse(bI.Ff(bba) || "null");
		return bk == null ? bF[bba] : bk
	};
	bE.ZA = function(bba, bA) {
		var bk = bE.lV(bba);
		if (bk == null) {
			bk = bA;
			bE.po(bba, bk)
		}
		return bk
	};
	bE.tC = function(bba) {
		delete bF[bba];
		bI.Ve(bba);
		return this
	};
	bE.ZB = function() {
		var vq = function(bfp, bba, bQ) {
			delete bQ[bba]
		};
		return function() {
			bm.dB(bF, vq);
			bI.UO();
			return this
		}
	}();
	bE.ZC = function(bw) {
		bw = bw || {};
		bm.bfq(bI.Uv(), function(bba) {
			bw[bba] = bE.lV(bba)
		});
		return bw
	};
	bR.hk.bZ({
		element: document,
		event: "storageready",
		oneventadd: function() {
			if (bI.Ur()) {
				document.onstorageready()
			}
		}
	});
	var TL = function() {
		var TG = function(bA, bba, bQ) {
			bI.EY(bba, JSON.stringify(bA));
			delete bQ[bba]
		};
		return function() {
			bm.dB(bF, TG)
		}
	}();
	bo.bW(document, "storageready", TL);
	bI.Us()
})();
(function() {
	var bh = NEJ.P,
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bs = bh("nej.ut"),
		nQ;
	if (!!bs.vv) return;
	bs.vv = NEJ.C();
	nQ = bs.vv.cg(bs.dE);
	nQ.cI = function() {
		var dn = +(new Date),
			iL = "dat-" + dn;
		return function() {
			this.dv();
			var bF = this.constructor[iL];
			if (!bF) {
				bF = {};
				this.constructor[iL] = bF
			}
			this.cs = bF
		}
	}();
	nQ.bL = function(bba) {
		return this.cs[bba]
	};
	nQ.tl = function(bba, bA) {
		var tM = this.cs[bba];
		this.cs[bba] = bA;
		bo.bJ(localCache, "cachechange", {
			key: bba,
			type: "set",
			oldValue: tM,
			newValue: bA
		});
		return this
	};
	nQ.gC = function(bba) {
		var tM = this.cs[bba];
		bm.Ay(this.cs, bba);
		bo.bJ(localCache, "cachechange", {
			key: bba,
			type: "delete",
			oldValue: tM,
			newValue: undefined
		});
		return tM
	};
	nQ.vy = function(mQ) {
		return NEJ.Q(this.cs, mQ)
	};
	window.localCache = bs.vv.bZ();
	bs.hk.bZ({
		element: localCache,
		event: "cachechange"
	})
})();
(function() {
	var bh = NEJ.P,
		fz = NEJ.R,
		bY = NEJ.F,
		bm = bh("nej.u"),
		bE = bh("nej.j"),
		bs = bh("nej.ut"),
		iL = "dat-" + +(new Date),
		gk;
	if (!!bs.vA) return;
	bs.vA = NEJ.C();
	gk = bs.vA.cg(bs.dE);
	gk.cI = function() {
		this.dv();
		this.cs = this.constructor[iL];
		if (!this.cs) {
			this.cs = {};
			this.cs[iL + "-l"] = {};
			this.constructor[iL] = this.cs
		}
	};
	gk.tP = function(bba) {
		return this.cs[bba]
	};
	gk.pt = function(bba, bA) {
		this.cs[bba] = bA
	};
	gk.TF = function(bba, ku) {
		var bk = this.tP(bba);
		if (bk == null) {
			bk = ku;
			this.pt(bba, bk)
		}
		return bk
	};
	gk.Tz = function(bba) {
		if (bba != null) {
			delete this.cs[bba];
			return
		}
		bm.dB(this.cs, function(bfp, bba) {
			if (bba == iL + "-l") return;
			this.Tz(bba)
		}, this)
	};
	gk.ZF = function(bba) {
		if (!!bE.tC) return bE.tC(bba)
	};
	gk.Sy = function(bba) {
		if (!!bE.lV) return bE.lV(bba)
	};
	gk.So = function(bba, bA) {
		if (!!bE.po) bE.po(bba, bA)
	};
	gk.EE = function(bba, ku) {
		var bk = this.ED(bba);
		if (bk == null) {
			bk = ku;
			this.vK(bba, bk)
		}
		return bk
	};
	gk.ED = function(bba) {
		var bk = this.tP(bba);
		if (bk != null) return bk;
		bk = this.Sy(bba);
		if (bk != null) this.pt(bba, bk);
		return bk
	};
	gk.vK = function(bba, bA) {
		this.So(bba, bA);
		this.pt(bba, bA)
	};
	gk.EB = function(bba) {
		if (bba != null) {
			delete this.cs[bba];
			if (!!bE.tC) bE.tC(bba);
			return
		}
		bm.dB(this.cs, function(bfp, bba) {
			if (bba == iL + "-l") return;
			this.EB(bba)
		}, this)
	};
	gk.ZG = function() {
		this.EB();
		return this
	};
	gk.vN = function(bba) {
		var bk = this.cs[iL + "-l"],
			bO = fz.slice.call(arguments, 1);
		bm.bfq(bk[bba], function(en) {
			try {
				en.apply(null, bO)
			} catch (ex) {
				console.error(ex.message);
				console.error(ex.stack)
			}
		});
		delete bk[bba]
	};
	gk.vO = function(bba, en) {
		en = en || bY;
		var bn = this.cs[iL + "-l"][bba];
		if (!bn) {
			bn = [en];
			this.cs[iL + "-l"][bba] = bn;
			return !1
		}
		bn.push(en);
		return !0
	};
	gk.RH = function(bn, cj, dF) {
		if (!bn) return !1;
		cj = parseInt(cj) || 0;
		dF = parseInt(dF) || 0;
		if (!dF) {
			if (!bn.loaded) return !1;
			dF = bn.length
		}
		if (!!bn.loaded) dF = Math.min(dF, bn.length - cj);
		for (var i = 0; i < dF; i++)
			if (!bn[cj + i]) return !1;
		return !0
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bm = bh("nej.u"),
		bs = bh("nej.ut"),
		bj, cf;
	if (!!bs.pB) return;
	bs.pB = NEJ.C();
	bj = bs.pB.cg(bs.vA);
	cf = bs.pB.ff;
	bj.cP = function(bf) {
		this.cS(bf);
		this.ju = bf.key || "id";
		this.dw = bf.data || bX;
		this.RF = !!bf.autogc;
		this.RE(bf.id)
	};
	bj.dx = function() {
		this.dA();
		if (!!this.gI) {
			this.Ey()
		}
	};
	bj.RE = function(bt) {
		var bF;
		if (!!bt) {
			bF = this.cs[bt];
			if (!bF) {
				bF = {};
				this.cs[bt] = bF
			}
		}
		bF = bF || this.cs;
		bF.hash = bF.hash || {};
		this.pD = bF
	};
	bj.Ey = function() {
		this.gI = window.clearTimeout(this.gI);
		var bQ = {};
		bm.dB(this.pD, function(bn, bba) {
			if (bba == "hash") return;
			if (!bm.eZ(bn)) return;
			bm.bfq(bn, function(bfp) {
				if (!bfp) return;
				bQ[bfp[this.ju]] = !0
			}, this)
		}, this);
		bm.dB(this.ur(), function(bfp, bt, eo) {
			if (!bQ[bt]) {
				delete eo[bt]
			}
		})
	};
	bj.RA = function() {
		if (!!this.gI) {
			this.gI = window.clearTimeout(this.gI)
		}
		this.gI = window.setTimeout(this.Ey.bq(this), 150)
	};
	bj.kP = function(bfp, Ex) {
		bfp = this.Rw(bfp, Ex) || bfp;
		if (!bfp) return null;
		var bba = bfp[this.ju];
		if (bba != null) {
			var pF = this.ur()[bba];
			if (!!pF) bfp = NEJ.X(pF, bfp);
			this.ur()[bba] = bfp
		}
		delete bfp.Ev;
		return bfp
	};
	bj.Rw = bY;
	bj.Eu = function(bba, bfp) {
		if (!bfp) return;
		if (!bm.eZ(bfp)) {
			var bn = this.gK(bba),
				bfp = this.kP(bfp, bba);
			if (!!bfp) bn.unshift(bfp);
			return
		}
		bm.gU(bfp, this.Eu.bq(this, bba))
	};
	bj.wl = function(bba, cR) {
		var bn = this.gK(bba);
		bn.length = Math.max(bn.length, cR);
		this.wn(bba);
		return this
	};
	bj.jE = function(bba) {
		return this.gK(bba).length
	};
	bj.wn = function(bba, uK) {
		this.gK(bba).loaded = uK != !1;
		return this
	};
	bj.uM = function(bba) {
		return !!this.gK(bba).loaded
	};
	bj.mP = function(bba, bn) {
		this.Et(bba);
		this.wt({
			key: bba,
			offset: 0,
			limit: bn.length + 1
		}, {
			list: bn,
			total: bn.length
		})
	};
	bj.gK = function() {
		var mN = function(bba) {
			return (bba || "") + (!bba ? "" : "-") + "list"
		};
		return function(bba) {
			var bba = mN(bba),
				bn = this.pD[bba];
			if (!bn) {
				bn = [];
				this.pD[bba] = bn
			}
			return bn
		}
	}();
	bj.ur = function() {
		var eo = this.pD.hash;
		if (!eo) {
			eo = {};
			this.pD.hash = eo
		}
		return eo
	};
	bj.Es = function() {
		var mN = function(bf) {
			return "r-" + bf.key
		};
		return function(bf) {
			var eN = NEJ.X({}, bf),
				kJ = mN(eN);
			if (!this.vO(kJ, this.bJ.bq(this))) {
				eN.rkey = kJ;
				eN.onload = this.QW.bq(this, eN);
				this.bJ("dopullrefresh", eN)
			}
			return this
		}
	}();
	bj.QW = function(bf, bn) {
		this.Eu(bf.key, bn);
		this.vN(bf.rkey, "onpullrefresh", bf)
	};
	bj.jC = function() {
		var mN = function(bf) {
			return "r-" + bf.key + "-" + bf.offset + "-" + bf.limit
		};
		return function(bf) {
			bf = bf || bX;
			var eN = {
					key: "" + bf.key || "",
					ext: bf.ext || null,
					data: bf.data || null,
					offset: parseInt(bf.offset) || 0,
					limit: parseInt(bf.limit) || 0
				},
				bn = this.gK(eN.key);
			if (this.RH(bn, eN.offset, eN.limit)) {
				this.bJ("onlistload", eN);
				return this
			}
			var kJ = mN(eN);
			if (!this.vO(kJ, this.bJ.bq(this))) {
				eN.rkey = kJ;
				eN.onload = this.wt.bq(this, eN);
				this.bJ("doloadlist", eN)
			}
			return this
		}
	}();
	bj.wt = function() {
		var mA = function(bfp, bfo, bn) {
			if (!!bfp) {
				return !0
			}
			bn.splice(bfo, 1)
		};
		return function(bf, bw) {
			bf = bf || bX;
			var bba = bf.key,
				cj = bf.offset,
				Er = this.gK(bba);
			var bn = bw || [];
			if (!bm.eZ(bn)) {
				bn = bw.result || bw.list || [];
				var cR = parseInt(bw.total);
				if (!isNaN(cR) || cR > bn.length) {
					this.wl(bba, cR)
				}
			}
			bm.bfq(bn, function(bfp, bfo) {
				Er[cj + bfo] = this.kP(bfp, bba)
			}, this);
			if (bn.length < bf.limit) {
				this.wn(bba);
				bm.gU(Er, mA)
			}
			this.vN(bf.rkey, "onlistload", bf)
		}
	}();
	bj.Et = function() {
		var mA = function(bfp, bfo, bn) {
			bn.splice(bfo, 1)
		};
		return function(bba) {
			var bn = this.gK(bba);
			bm.gU(bn, mA);
			this.wn(bba, !1);
			if (this.RF) {
				this.RA()
			}
			return this
		}
	}();
	bj.QR = function(bfp, Ex) {
		return !bfp.Ev
	};
	bj.eW = function(bt) {
		return this.ur()[bt]
	};
	bj.baT = function(bt) {
		var bfp = this.eW(bt);
		if (!!bfp) bfp.Ev = !0
	};
	bj.Ep = function() {
		var mN = function(bf) {
			return "r-" + bf.key + "-" + bf.id
		};
		return function(bf) {
			bf = bf || bX;
			var bt = bf[this.ju],
				eN = {
					id: bt,
					ext: bf.ext,
					data: bf.data || {},
					key: "" + bf.key || ""
				};
			bfp = this.eW(bt);
			eN.data[this.ju] = bt;
			if (!!bfp && this.QR(bfp, eN.key)) {
				this.bJ("onitemload", eN);
				return this
			}
			var kJ = mN(eN);
			if (!this.vO(kJ, this.bJ.bq(this))) {
				eN.rkey = kJ;
				eN.onload = this.QE.bq(this, eN);
				this.bJ("doloaditem", eN)
			}
			return this
		}
	}();
	bj.QE = function(bf, bfp) {
		bf = bf || bX;
		this.kP(bfp, bf.key);
		this.vN(bf.rkey, "onitemload", bf)
	};
	bj.wG = function(bf) {
		bf = NEJ.X({}, bf);
		bf.onload = this.En.bq(this, bf);
		this.bJ("doadditem", bf)
	};
	bj.En = function(bf, bfp) {
		var bba = bf.key;
		bfp = this.kP(bfp, bba);
		if (!!bfp) {
			var gW = 0,
				bn = this.gK(bba);
			if (!bf.push) {
				gW = -1;
				var cj = bf.offset || 0;
				bn.splice(cj, 0, bfp)
			} else if (bn.loaded) {
				gW = 1;
				bn.push(bfp)
			} else {
				bn.length++
			}
		}
		var bi = {
			key: bba,
			flag: gW,
			data: bfp,
			action: "add",
			ext: bf.ext
		};
		this.bJ("onitemadd", bi);
		return bi
	};
	bj.PV = function(bf) {
		bf = NEJ.X({}, bf);
		bf.onload = this.Ej.bq(this, bf);
		this.bJ("dodeleteitem", bf)
	};
	bj.Ej = function(bf, PN) {
		var bfp, bba = bf.key;
		if (!!PN) {
			bfp = this.eW(bf.id) || null;
			var bt = bf.id,
				PH = this.ju,
				bn = this.gK(bba),
				bfo = bm.fM(bn, function(pF) {
					return !!pF && pF[PH] == bt
				});
			if (bfo >= 0) bn.splice(bfo, 1)
		}
		var bi = {
			key: bba,
			data: bfp,
			action: "delete",
			ext: bf.ext
		};
		this.bJ("onitemdelete", bi);
		return bi
	};
	bj.PE = function(bf) {
		bf = NEJ.X({}, bf);
		bf.onload = this.PB.bq(this, bf);
		this.bJ("doupdateitem", bf)
	};
	bj.PB = function(bf, bfp) {
		var bba = bf.key;
		if (!!bfp) bfp = this.kP(bfp, bba);
		var bi = {
			key: bba,
			data: bfp,
			action: "update",
			ext: bf.ext
		};
		this.bJ("onitemupdate", bi);
		return bi
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bm = bh("nej.u"),
		bs = bh("nej.ut"),
		bj;
	if (!!bs.wO) return;
	bs.wO = NEJ.C();
	bj = bs.wO.cg(bs.pB);
	bj.cP = function(bf) {
		this.cS(bf);
		this.vw({
			doloadlist: this.rE.bq(this),
			doloaditem: this.Eh.bq(this),
			doadditem: this.Ef.bq(this),
			dodeleteitem: this.rG.bq(this),
			doupdateitem: this.rH.bq(this),
			dopullrefresh: this.DZ.bq(this)
		})
	};
	bj.rE = bY;
	bj.DZ = bY;
	bj.Eh = bY;
	bj.Ef = bY;
	bj.rG = bY;
	bj.rH = bY
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		bE = bh("nej.j"),
		bR = bh("nej.ut"),
		bz = bh("nm.x"),
		bD = bh("nm.d"),
		bj, cf;
	bD.gY = NEJ.C();
	bj = bD.gY.cg(bR.wO);
	bj.dT = function() {
		var DY = location.protocol + "//" + location.host;
		var Pe = function(bP, bk) {
			var bQ = {
				conf: {},
				data: {},
				urls: []
			};
			bm.bfq(bP, function(bba, bfo, bn) {
				var bu = bD.bL(bba);
				if (!bu) return;
				var wY = DX(bu.url, bk[bba]);
				bQ.urls.push(wY);
				bQ.conf[wY] = bu;
				bQ.data[wY] = JSON.stringify(bk[bba] == null ? "" : bk[bba])
			});
			return bQ
		};
		var DX = function(bC, bk) {
			return bC.replace(/\{(.*?)\}/gi, function($1, $2) {
				return bk[$2] || $1
			})
		};
		var DW = function(bu, bf, bi) {
			bo.bJ(window, "requesterror", bi);
			if (!!bi.stopped) return;
			var xg = bu.onerror || bf.onerror;
			if (bm.dW(xg)) {
				this.bJ(xg, bi, bf)
			} else {
				(xg || bY).call(this, bi, bf)
			}
			var bi = {
				result: bi,
				option: bf
			};
			this.bJ("onerror", bi);
			if (!bi.stopped)(bu.onmessage || bY).call(this, bi.result.code, bi.result)
		};
		var DV = function(bN, bu, bf) {
			var bw = bN;
			if (bm.es(bu.format)) {
				bw = bu.format.call(this, bN, bf)
			}
			return bw
		};
		var qH = function(bN, bu, bf, jf) {
			if (bm.es(bu.beforeload)) {
				bu.beforeload.call(this, bN, bf, bu)
			}
			if (bN && bN.code != null && bN.code != 200) {
				DW.call(this, bu, bf, {
					key: bf.key,
					code: bN.code,
					message: bN.message || "",
					captchaId: bN.captchaId
				});
				return
			}
			var bw = bN;
			if (!jf) {
				bw = DV.call(this, bN, bu, bf)
			} else if (bm.es(bu.format)) {
				var xi = [];
				bm.bfq(jf.urls, function(bC) {
					xi.push(DV.call(this, bN[bC], jf.conf[bC], bf))
				}, this);
				xi.push(bf);
				bw = bu.format.apply(this, xi)
			}
			var mh = bu.onload || bf.onload,
				DU = bu.finaly || bf.finaly || bY;
			if (bm.dW(mh)) {
				DU.call(this, this.bJ(mh, bw), bf)
			} else {
				DU.call(this, (mh || bY).call(this, bw), bf)
			}
		};
		var Ai = function(bu, bf, eL) {
			DW.call(this, bu, bf, {
				key: bf.key,
				code: eL.code || -1,
				message: eL.message || ""
			})
		};
		return function(bu, bf) {
			if (bm.dW(bu)) {
				bu = bD.bL(bu)
			}
			delete bf.value;
			(bu.filter || bY).call(this, bf, bu);
			var bN = bf.value;
			if (!!bN) {
				qH.call(this, bN, bu, bf);
				return
			}
			var bC, bk = bf.data || bX,
				kw = {
					cookie: !0,
					type: bu.rtype || "json",
					method: bu.type || "POST",
					onerror: Ai.bq(this, bu, bf),
					noescape: bu.noescape
				};
			if (bm.eZ(bu.url)) {
				var jf = Pe(bu.url, bk);
				bC = DY + "/api/batch";
				kw.data = bm.fr(jf.data);
				kw.onload = qH.jh(this, bu, bf, jf);
				kw.headers = {
					"batch-method": "POST"
				};
				delete jf.data
			} else {
				var fu = bu.url.indexOf(":") < 0 ? DY : "";
				bC = DX(fu + bu.url, bk);
				kw.data = bm.fr(bf.data);
				kw.onload = qH.jh(this, bu, bf)
			}
			if (kw.method == "GET") kw.query = kw.data;
			return bE.cN(bC, kw)
		}
	}();
	bj.nH = function() {
		var dO = /^get|list|pull$/i;
		return function(DT, bf) {
			var bba = bf.key,
				bu = bD.bL(bba.split("-")[0] + "-" + DT);
			if (dO.test(DT) && bba.indexOf("post-") < 0) bu.type = "GET";
			this.dT(bu, bf)
		}
	}();
	bj.ZJ = function(bba, bn) {
		var cR = bn.length;
		this.wt({
			key: bba,
			offset: 0,
			limit: cR + 1
		}, {
			list: bn,
			total: cR
		})
	};
	bj.rE = function(bf) {
		this.nH("list", bf)
	};
	bj.Eh = function(bf) {
		this.nH("get", bf)
	};
	bj.DZ = function(bf) {
		this.nH("pull", bf)
	};
	bj.Ef = function(bf) {
		this.nH("add", bf)
	};
	bj.rG = function(bf) {
		this.nH("del", bf)
	};
	bj.rH = function(bf) {
		this.nH("update", bf)
	};
	bj.Ox = function(bfp) {
		this.kP(bfp)
	};
	bR.hk.bZ({
		element: window,
		event: "requesterror"
	})
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bo = bh("nej.v"),
		bR = bh("nej.ut"),
		bD = bh("nm.d"),
		xq = {},
		bj, cf;
	var iD = function(bw, bf) {
		bw.conf = bf.conf;
		return bw
	};
	bD.hm({
		"res-mv-get": {
			type: "GET",
			url: "/api/v1/mv/detail",
			format: function(bN, bf) {
				return iD({
					mv: bN
				}, bf)
			}
		},
		"res-song-get": {
			type: "GET",
			url: "/api/song/detail/",
			format: function(bw, bf) {
				if (!!bw.songs && bw.songs.length > 0) bw.song = bw.songs[0];
				else bw.song = xq;
				delete bw.songs;
				return iD(bw, bf)
			},
			filter: function(bf) {
				bf.data.ids = "[" + bf.data.id + "]"
			}
		},
		"res-program-get": {
			type: "GET",
			url: "/api/dj/program/detail",
			format: iD
		},
		"res-album-get": {
			type: "GET",
			url: "/api/album/{id}",
			format: iD
		},
		"res-playlist-get": {
			type: "GET",
			url: "/api/playlist/detail",
			format: function(bw, bf) {
				bw.playlist = bw.result;
				delete bw.result;
				return iD(bw, bf)
			}
		},
		"res-mv-play": {
			type: "GET",
			url: "/api/song/mv/play",
			format: iD
		},
		"res-playlist-play": {
			type: "GET",
			url: "/api/playlist/update/playcount",
			format: iD
		},
		"res-program-play": {
			type: "GET",
			url: "/api/dj/program/listen",
			format: iD
		},
		"res-djradio-get": {
			type: "GET",
			url: "/api/dj/program/byradio",
			filter: function(bf) {
				var bn = bf.data.id.split("-");
				bf.data.radioId = bn[0];
				bf.data.asc = bn[1] == 2;
				bf.data.limit = 1e3;
				delete bf.data.id
			},
			format: function(bN, bf) {
				var Oo = {
					id: bf.data.radioId,
					programs: bN.programs
				};
				return iD({
					djradio: Oo
				}, bf)
			}
		},
		"res-hotSongs-get": {
			type: "GET",
			url: "/api/artist/{id}",
			format: iD
		},
		"res-lyric-get": {
			type: "GET",
			url: "/api/song/lyric",
			filter: function(bf) {
				bf.data.lv = 0
			},
			format: function(bw, bf) {
				var rW = {
					lyric: "",
					nolyric: true
				};
				if (bw.code == 200 && bw.lrc && bw.lrc.lyric) {
					rW.lyric = bw.lrc.lyric;
					rW.nolyric = false
				} else {
					rW.nolyric = true
				}
				return iD({
					lyric: rW
				}, bf)
			}
		}
	});
	bD.nF = NEJ.C();
	bj = bD.nF.cg(bD.gY);
	bj.Oi = function(bv, dM) {
		return this.tP(this.rZ(bv, dM))
	};
	bj.DR = function(bv, dM, bf) {
		bf = bf || {};
		var bk = this.tP(this.rZ(bv, dM));
		if (bk && (bv != 13 && bv != 19 || bf.conf && bf.conf.useCache)) {
			this.bJ("onresourceload", bv, dM, bk, bf.conf);
			return
		}
		bf.data = {
			id: dM
		};
		bf.onload = this.NZ.bq(this, bv, dM);
		bf.onerror = this.NU.bq(this, bv, dM);
		this.dT("res-" + this.kz(bv) + "-get", bf)
	};
	bj.NZ = function(bv, dM, bw) {
		var bk = bw[this.kz(bv)];
		this.pt(this.rZ(bv, dM), bk);
		this.bJ("onresourceload", bv, dM, bk, bw.conf)
	};
	bj.NU = function(bv, dM, bw, bf) {
		if (bw.code != 404) {
			this.bJ("onresourceerror", bv, dM, bw.code);
			return
		}
		this.pt(this.rZ(bv, dM), xq);
		this.bJ("onresourceload", bv, dM, xq, bf.conf)
	};
	bj.ZK = function(bv, bf) {
		this.dT("res-" + this.kz(bv) + "-play", bf)
	};
	bj.rZ = function(bv, dM) {
		return "res-" + this.kz(bv) + "-" + dM
	};
	bj.kz = function(bv) {
		var bQ = {
			2: "hotSongs",
			13: "playlist",
			17: "program",
			18: "song",
			19: "album",
			21: "mv",
			41: "lyric",
			70: "djradio"
		};
		return bQ[bv]
	};
	bD.nF.ZL = function(bv, dM) {
		if (!this.eG) this.eG = bD.nF.bZ({});
		return this.eG.Oi(bv, dM)
	}
})();
(function() {
	var o = !0,
		w = null;
	(function(B) {
		function v(a) {
			if ("bug-string-char-index" == a) return "a" != "a" [0];
			var f, c = "json" == a;
			if (c || "json-stringify" == a || "json-parse" == a) {
				if ("json-stringify" == a || c) {
					var d = k.stringify,
						b = "function" == typeof d && l;
					if (b) {
						(f = function() {
							return 1
						}).toJSON = f;
						try {
							b = "0" === d(0) && "0" === d(new Number) && '""' == d(new String) && d(m) === r && d(r) === r && d() === r && "1" === d(f) && "[1]" == d([f]) && "[null]" == d([r]) && "null" == d(w) && "[null,null,null]" == d([r, m, w]) && '{"a":[1,true,false,null,"\\u0000\\b\\n\\f\\r\\t"]}' == d({
								a: [f, o, !1, w, "\0\b\n\f\r\t"]
							}) && "1" === d(w, f) && "[\n 1,\n 2\n]" == d([1, 2], w, 1) && '"-271821-04-20T00:00:00.000Z"' == d(new Date(-864e13)) && '"+275760-09-13T00:00:00.000Z"' == d(new Date(864e13)) && '"-000001-01-01T00:00:00.000Z"' == d(new Date(-621987552e5)) && '"1969-12-31T23:59:59.999Z"' == d(new Date(-1))
						} catch (n) {
							b = !1
						}
					}
					if (!c) return b
				}
				if ("json-parse" == a || c) {
					a = k.parse;
					if ("function" == typeof a) try {
						if (0 === a("0") && !a(!1)) {
							f = a('{"a":[1,true,false,null,"\\u0000\\b\\n\\f\\r\\t"]}');
							var e = 5 == f.a.length && 1 === f.a[0];
							if (e) {
								try {
									e = !a('"\t"')
								} catch (g) {}
								if (e) try {
									e = 1 !== a("01")
								} catch (i) {}
							}
						}
					} catch (O) {
						e = !1
					}
					if (!c) return e
				}
				return b && e
			}
		}
		var m = {}.toString,
			p, C, r, D = typeof define === "function" && define.amd,
			k = "object" == typeof exports && exports;
		k || D ? "object" == typeof JSON && JSON ? k ? (k.stringify = JSON.stringify, k.parse = JSON.parse) : k = JSON : D && (k = B.JSON = {}) : k = B.JSON || (B.JSON = {});
		var l = new Date(-0xc782b5b800cec);
		try {
			l = -109252 == l.getUTCFullYear() && 0 === l.getUTCMonth() && 1 === l.getUTCDate() && 10 == l.getUTCHours() && 37 == l.getUTCMinutes() && 6 == l.getUTCSeconds() && 708 == l.getUTCMilliseconds()
		} catch (P) {}
		if (!v("json")) {
			var s = v("bug-string-char-index");
			if (!l) var t = Math.floor,
				J = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334],
				z = function(a, f) {
					return J[f] + 365 * (a - 1970) + t((a - 1969 + (f = +(f > 1))) / 4) - t((a - 1901 + f) / 100) + t((a - 1601 + f) / 400)
				};
			if (!(p = {}.hasOwnProperty)) p = function(a) {
				var f = {},
					c;
				if ((f.__proto__ = w, f.__proto__ = {
						toString: 1
					}, f).toString != m) p = function(a) {
					var f = this.__proto__,
						a = a in (this.__proto__ = w, this);
					this.__proto__ = f;
					return a
				};
				else {
					c = f.constructor;
					p = function(a) {
						var f = (this.constructor || c).prototype;
						return a in this && !(a in f && this[a] === f[a])
					}
				}
				f = w;
				return p.call(this, a)
			};
			var K = {
				"boolean": 1,
				number: 1,
				string: 1,
				"undefined": 1
			};
			C = function(a, f) {
				var c = 0,
					b, h, n;
				(b = function() {
					this.valueOf = 0
				}).prototype.valueOf = 0;
				h = new b;
				for (n in h) p.call(h, n) && c++;
				b = h = w;
				if (c) c = c == 2 ? function(a, f) {
					var c = {},
						b = m.call(a) == "[object Function]",
						d;
					for (d in a) !(b && d == "prototype") && !p.call(c, d) && (c[d] = 1) && p.call(a, d) && f(d)
				} : function(a, f) {
					var c = m.call(a) == "[object Function]",
						b, d;
					for (b in a) !(c && b == "prototype") && p.call(a, b) && !(d = b === "constructor") && f(b);
					(d || p.call(a, b = "constructor")) && f(b)
				};
				else {
					h = ["valueOf", "toString", "toLocaleString", "propertyIsEnumerable", "isPrototypeOf", "hasOwnProperty", "constructor"];
					c = function(a, f) {
						var c = m.call(a) == "[object Function]",
							b, d;
						if (d = !c)
							if (d = typeof a.constructor != "function") {
								d = typeof a.hasOwnProperty;
								d = d == "object" ? !!a.hasOwnProperty : !K[d]
							}
						d = d ? a.hasOwnProperty : p;
						for (b in a) !(c && b == "prototype") && d.call(a, b) && f(b);
						for (c = h.length; b = h[--c]; d.call(a, b) && f(b));
					}
				}
				c(a, f)
			};
			if (!v("json-stringify")) {
				var L = {
						92: "\\\\",
						34: '\\"',
						8: "\\b",
						12: "\\f",
						10: "\\n",
						13: "\\r",
						9: "\\t"
					},
					u = function(a, f) {
						return ("000000" + (f || 0)).slice(-a)
					},
					G = function(a) {
						var f = '"',
							b = 0,
							d = a.length,
							h = d > 10 && s,
							n;
						for (h && (n = a.split("")); b < d; b++) {
							var e = a.charCodeAt(b);
							switch (e) {
								case 8:
								case 9:
								case 10:
								case 12:
								case 13:
								case 34:
								case 92:
									f = f + L[e];
									break;
								default:
									if (e < 32) {
										f = f + ("\\u00" + u(2, e.toString(16)));
										break
									}
									f = f + (h ? n[b] : s ? a.charAt(b) : a[b])
							}
						}
						return f + '"'
					},
					E = function(a, b, c, d, h, n, e) {
						var g = b[a],
							i, j, k, l, q, s, v, x, y;
						try {
							g = b[a]
						} catch (A) {}
						if (typeof g == "object" && g) {
							i = m.call(g);
							if (i == "[object Date]" && !p.call(g, "toJSON"))
								if (g > -1 / 0 && g < 1 / 0) {
									if (z) {
										k = t(g / 864e5);
										for (i = t(k / 365.2425) + 1970 - 1; z(i + 1, 0) <= k; i++);
										for (j = t((k - z(i, 0)) / 30.42); z(i, j + 1) <= k; j++);
										k = 1 + k - z(i, j);
										l = (g % 864e5 + 864e5) % 864e5;
										q = t(l / 36e5) % 24;
										s = t(l / 6e4) % 60;
										v = t(l / 1e3) % 60;
										l = l % 1e3
									} else {
										i = g.getUTCFullYear();
										j = g.getUTCMonth();
										k = g.getUTCDate();
										q = g.getUTCHours();
										s = g.getUTCMinutes();
										v = g.getUTCSeconds();
										l = g.getUTCMilliseconds()
									}
									g = (i <= 0 || i >= 1e4 ? (i < 0 ? "-" : "+") + u(6, i < 0 ? -i : i) : u(4, i)) + "-" + u(2, j + 1) + "-" + u(2, k) + "T" + u(2, q) + ":" + u(2, s) + ":" + u(2, v) + "." + u(3, l) + "Z"
								} else g = w;
							else if (typeof g.toJSON == "function" && (i != "[object Number]" && i != "[object String]" && i != "[object Array]" || p.call(g, "toJSON"))) g = g.toJSON(a)
						}
						c && (g = c.call(b, a, g));
						if (g === w) return "null";
						i = m.call(g);
						if (i == "[object Boolean]") return "" + g;
						if (i == "[object Number]") return g > -1 / 0 && g < 1 / 0 ? "" + g : "null";
						if (i == "[object String]") return G("" + g);
						if (typeof g == "object") {
							for (a = e.length; a--;)
								if (e[a] === g) throw TypeError();
							e.push(g);
							x = [];
							b = n;
							n = n + h;
							if (i == "[object Array]") {
								j = 0;
								for (a = g.length; j < a; y || (y = o), j++) {
									i = E(j, g, c, d, h, n, e);
									x.push(i === r ? "null" : i)
								}
								a = y ? h ? "[\n" + n + x.join(",\n" + n) + "\n" + b + "]" : "[" + x.join(",") + "]" : "[]"
							} else {
								C(d || g, function(a) {
									var b = E(a, g, c, d, h, n, e);
									b !== r && x.push(G(a) + ":" + (h ? " " : "") + b);
									y || (y = o)
								});
								a = y ? h ? "{\n" + n + x.join(",\n" + n) + "\n" + b + "}" : "{" + x.join(",") + "}" : "{}"
							}
							e.pop();
							return a
						}
					};
				k.stringify = function(a, b, c) {
					var d, h, j;
					if (typeof b == "function" || typeof b == "object" && b)
						if (m.call(b) == "[object Function]") h = b;
						else if (m.call(b) == "[object Array]") {
						j = {};
						for (var e = 0, g = b.length, i; e < g; i = b[e++], (m.call(i) == "[object String]" || m.call(i) == "[object Number]") && (j[i] = 1));
					}
					if (c)
						if (m.call(c) == "[object Number]") {
							if ((c = c - c % 1) > 0) {
								d = "";
								for (c > 10 && (c = 10); d.length < c; d = d + " ");
							}
						} else m.call(c) == "[object String]" && (d = c.length <= 10 ? c : c.slice(0, 10));
					return E("", (i = {}, i[""] = a, i), h, j, d, "", [])
				}
			}
			if (!v("json-parse")) {
				var M = String.fromCharCode,
					N = {
						92: "\\",
						34: '"',
						47: "/",
						98: "\b",
						116: "\t",
						110: "\n",
						102: "\f",
						114: "\r"
					},
					b, A, j = function() {
						b = A = w;
						throw SyntaxError()
					},
					q = function() {
						for (var a = A, f = a.length, c, d, h, k, e; b < f;) {
							e = a.charCodeAt(b);
							switch (e) {
								case 9:
								case 10:
								case 13:
								case 32:
									b++;
									break;
								case 123:
								case 125:
								case 91:
								case 93:
								case 58:
								case 44:
									c = s ? a.charAt(b) : a[b];
									b++;
									return c;
								case 34:
									c = "@";
									for (b++; b < f;) {
										e = a.charCodeAt(b);
										if (e < 32) j();
										else if (e == 92) {
											e = a.charCodeAt(++b);
											switch (e) {
												case 92:
												case 34:
												case 47:
												case 98:
												case 116:
												case 110:
												case 102:
												case 114:
													c = c + N[e];
													b++;
													break;
												case 117:
													d = ++b;
													for (h = b + 4; b < h; b++) {
														e = a.charCodeAt(b);
														e >= 48 && e <= 57 || e >= 97 && e <= 102 || e >= 65 && e <= 70 || j()
													}
													c = c + M("0x" + a.slice(d, b));
													break;
												default:
													j()
											}
										} else {
											if (e == 34) break;
											e = a.charCodeAt(b);
											for (d = b; e >= 32 && e != 92 && e != 34;) e = a.charCodeAt(++b);
											c = c + a.slice(d, b)
										}
									}
									if (a.charCodeAt(b) == 34) {
										b++;
										return c
									}
									j();
								default:
									d = b;
									if (e == 45) {
										k = o;
										e = a.charCodeAt(++b)
									}
									if (e >= 48 && e <= 57) {
										for (e == 48 && (e = a.charCodeAt(b + 1), e >= 48 && e <= 57) && j(); b < f && (e = a.charCodeAt(b), e >= 48 && e <= 57); b++);
										if (a.charCodeAt(b) == 46) {
											for (h = ++b; h < f && (e = a.charCodeAt(h), e >= 48 && e <= 57); h++);
											h == b && j();
											b = h
										}
										e = a.charCodeAt(b);
										if (e == 101 || e == 69) {
											e = a.charCodeAt(++b);
											(e == 43 || e == 45) && b++;
											for (h = b; h < f && (e = a.charCodeAt(h), e >= 48 && e <= 57); h++);
											h == b && j();
											b = h
										}
										return +a.slice(d, b)
									}
									k && j();
									if (a.slice(b, b + 4) == "true") {
										b = b + 4;
										return o
									}
									if (a.slice(b, b + 5) == "false") {
										b = b + 5;
										return false
									}
									if (a.slice(b, b + 4) == "null") {
										b = b + 4;
										return w
									}
									j()
							}
						}
						return "$"
					},
					F = function(a) {
						var b, c;
						a == "$" && j();
						if (typeof a == "string") {
							if ((s ? a.charAt(0) : a[0]) == "@") return a.slice(1);
							if (a == "[") {
								for (b = [];; c || (c = o)) {
									a = q();
									if (a == "]") break;
									if (c)
										if (a == ",") {
											a = q();
											a == "]" && j()
										} else j();
									a == "," && j();
									b.push(F(a))
								}
								return b
							}
							if (a == "{") {
								for (b = {};; c || (c = o)) {
									a = q();
									if (a == "}") break;
									if (c)
										if (a == ",") {
											a = q();
											a == "}" && j()
										} else j();
										(a == "," || typeof a != "string" || (s ? a.charAt(0) : a[0]) != "@" || q() != ":") && j();
									b[a.slice(1)] = F(q())
								}
								return b
							}
							j()
						}
						return a
					},
					I = function(a, b, c) {
						c = H(a, b, c);
						c === r ? delete a[b] : a[b] = c
					},
					H = function(a, b, c) {
						var d = a[b],
							h;
						if (typeof d == "object" && d)
							if (m.call(d) == "[object Array]")
								for (h = d.length; h--;) I(d, h, c);
							else C(d, function(a) {
								I(d, a, c)
							});
						return c.call(a, b, d)
					};
				k.parse = function(a, f) {
					var c, d;
					b = 0;
					A = "" + a;
					c = F(q());
					q() != "$" && j();
					b = A = w;
					return f && m.call(f) == "[object Function]" ? H((d = {}, d[""] = c, d), "", f) : c
				}
			}
		}
		D && define(function() {
			return k
		})
	})(this)
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bR = bh("nej.ut"),
		bD = bh("nm.d"),
		zy = /^[1-9][0-9]*$/,
		bj, cf;
	var LOCAL_LOG_KEY = "local-log";
	bD.hm({
		"bi-log": {
			url: "/api/log/web"
		},
		"bi-batch-log": {
			url: "/api/batchlog/web"
		},
		"plus-mv-count": {
			url: "/api/song/mv/play"
		},
		"plus-song-count": {
			url: "/api/song/play"
		},
		"plus-dj-count": {
			type: "GET",
			url: "/api/dj/program/listen"
		},
		"plus-playlist-count": {
			type: "GET",
			url: "/api/playlist/update/playcount"
		}
	});
	bD.qI = NEJ.C();
	bj = bD.qI.cg(bD.gY);
	bj.Dk = function(cC, bu) {
		if (!cC || !bu) return;
		var bk = {
			action: cC,
			json: bm.dW(bu) ? bu : JSON.stringify(bu)
		};
		this.dT("bi-log", {
			data: bk
		})
	};
	bj.TJ = function(hD) {
		this.dT("bi-batch-log", {
			data: {
				data: JSON.stringify(hD)
			}
		})
	};
	bj.TD = function(bu) {
		if (!bu || !bu.type || !bu.rid) return;
		var go = bu.type;
		if (zy.test(go)) {
			go = this.kz(go)
		}
		if (!go) return;
		if (go == "playlist") go = "list";
		var bk = {
			action: "search",
			json: JSON.stringify({
				type: go,
				id: bu.rid,
				keyword: bu.keyword
			})
		};
		this.dT("bi-log", {
			data: bk
		})
	};
	bj.Tq = function() {
		var Sq = /^\/m\/(song|album|playlist)\?id=(\d+)/;
		return function(bu) {
			if (!bu || !bu.type || !bu.rid) return;
			if (bu.play === undefined) bu.play = true;
			var go = bu.type;
			if (zy.test(go)) {
				go = this.kz(go)
			}
			if (!go) return;
			if (go == "playlist") go = "list";
			var bN = {
				id: bu.rid,
				type: go
			};
			if (go == "song" && bu.source) {
				bN.source = this.Df(bu.source);
				if (!!bu.sourceid) bN.sourceid = bu.sourceid
			}
			var bk = {
				action: !bu.play ? "addto" : "play",
				json: JSON.stringify(bN)
			};
			this.dT("bi-log", {
				data: bk
			});
			if (go == "song" && bu.hash && bu.hash.match(Sq)) {
				bk.json = JSON.stringify({
					type: RegExp.$1,
					id: RegExp.$2
				});
				this.dT("bi-log", {
					data: bk
				})
			}
		}
	}();
	bj.bae = function(bt, dg, cD, zJ) {
		var bN = {
			type: "song",
			wifi: 0,
			download: 0
		};
		var RL = {
			1: "ui",
			2: "playend",
			3: "interrupt",
			4: "exception"
		};
		bN.id = bt;
		bN.time = Math.round(dg);
		bN.end = bm.dW(zJ) ? zJ : RL[zJ] || "";
		if (cD && cD.fid) {
			bN.source = this.Df(cD.fid);
			bN.sourceId = cD.fdata
		}
		this.Dk("play", bN)
	};
	bj.bai = function(bv, dM) {
		if (!bv || !dM) return;
		if (zy.test(bv)) bv = this.kz(bv);
		if (bv != "playlist" && bv != "dj") return;
		var bu = bD.bL("plus-" + bv + "-count");
		if (!bu) return !1;
		this.dT(bu, {
			data: {
				id: dM
			}
		});
		var bF = this.TF("play-hist-" + bv, []);
		if (bm.fM(bF, dM) < 0) {
			bF.push(dM);
			return !0
		}
		return !1
	};
	bj.kz = function(bv) {
		var bQ = {
			1: "user",
			2: "artist",
			13: "playlist",
			17: "dj",
			18: "song",
			19: "album",
			21: "mv",
			31: "toplist"
		};
		return bQ[bv]
	};
	bj.Df = function(qO) {
		var bQ = {
			1: "user",
			2: "artist",
			13: "list",
			17: "dj",
			18: "song",
			19: "album",
			21: "mv",
			31: "toplist",
			32: "search",
			33: "search",
			34: "event",
			35: "msg"
		};
		return bQ[qO]
	};
	bj.baj = function(QS) {
		var hD = this.EE(LOCAL_LOG_KEY, []);
		hD.unshift(QS);
		if (hD.length > 200) {
			hD.length = 200
		}
		this.vK(LOCAL_LOG_KEY, hD)
	};
	bj.QM = function() {
		return this.ED(LOCAL_LOG_KEY)
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bY = NEJ.F,
		bo = bh("nej.v"),
		bE = bh("nej.j"),
		bR = bh("nej.ut"),
		bg = bh("nej.e"),
		bm = bh("nej.u"),
		bV = bh("nm.l"),
		bz = bh("nm.x"),
		bD = bh("nm.d");
	if (!bD.nF) return;
	var bF = bD.nF.bZ({
		onresourceload: NP
	});
	var bfn = bD.qI.nu();

	function NP(bv, dM, bk, bu) {
		var bn = [];
		switch (parseInt(bv)) {
			case 2:
				bn = bk;
				break;
			case 13:
				bn = bk.tracks;
				break;
			case 18:
				bn.push(bk);
				break;
			case 19:
				bn = bk.songs;
				break;
			case 21:
				if (bk.mp && bk.mp.fee && bk.mp.pl <= 0) {
					bz.NO(bk.data.id, bk.mp.fee);
					return
				}
				break
		}
		if (bz.DO(bn, true, null, bv == 19 ? {
				source: "album",
				sourceid: dM
			} : null) == 0) {
			return
		}
		bz.yh({
			clazz: "m-layer-w2",
			bubble: !1,
			message: bu.message
		})
	}

	function NI(bi, hG, oC, tG) {
		tG = tG || {};
		if (bi.action == "ok") {
			if (oC) {
				location.dispatch2("/payfee?songId=" + oC)
			} else {
				location.dispatch2("/payfee?fee=" + hG || 1)
			}
			bfn.Dk("click", {
				type: "tobuyvip",
				name: "box",
				source: tG.source || "song",
				sourceid: tG.sourceid || oC || 0
			})
		} else if (bi.action == "song") {
			location.dispatch2("/payfee?singleSong=true&songId=" + oC);
			bfn.Dk("click", {
				type: "tobuyone",
				name: "box",
				source: tG.source || "song",
				sourceid: tG.sourceid || oC || 0
			})
		}
	}
	bz.su = function(cG, bt, bv) {
		cG = cG || "由于版权保护，您所在的地区暂时无法使用。";
		if (bt && bv) {
			bF.DR(bv, bt, {
				conf: {
					message: cG,
					useCache: bv != 18
				}
			})
		} else {
			bz.yh({
				clazz: "m-layer-w2",
				bubble: !1,
				message: cG,
				btntxt: "知道了"
			})
		}
	};
	bz.sv = function(hG, oC, bv, tG) {
		var ci, hZ = "m-popup-info",
			xM = "单首购买",
			DN = "马上去订购",
			eI = "唱片公司要求，当前歌曲须付费使用。";
		try {
			ci = top.api.feeMessage || {}
		} catch (e) {
			ci = {}
		}
		if (hG == 1 || hG == 8 || hG == 16) {
			if (bv == "song") {
				hZ = "m-popup-song-buy";
				eI = ci["vip2"] || eI;
				DN = ci["vip2button"] || "包月购买";
				xM = ci["vip2link"] || xM
			} else {
				eI = ci["vip"] || eI
			}
		} else if (hG == 4) {
			eI = ci["album"] || eI
		} else {
			eI = ci["unknow"] || eI
		}
		bz.Fr({
			clazz: "m-layer-w5",
			html: bg.eH(hZ, {
				songTxt: xM,
				tip: eI,
				oktext: DN,
				cctext: "以后再说"
			}),
			onaction: NI.jh(null, hG, oC, tG)
		})
	};
	bz.ND = function(Nv, Nu) {
		bz.MH({
			title: "提示",
			message: "唱片公司要求，该歌曲须下载后播放",
			btnok: "下载",
			btncc: "取消",
			okstyle: "u-btn2-w1",
			ccstyle: "u-btn2-w1",
			action: function(bv) {
				if (bv == "ok") {
					bz.Fi({
						id: Nv,
						type: Nu
					})
				}
			}
		})
	};
	bz.NO = function(Ns, hG) {
		var ci, eI = "唱片公司要求，当前歌曲须付费使用。";
		try {
			ci = top.api.feeMessage || {}
		} catch (e) {
			ci = {}
		}
		if (hG == 1 || hG == 8) {
			eI = ci["vip"] || eI
		} else if (hG == 4) {
			eI = ci["album"] || eI
		} else {
			eI = ci["unknow"] || eI
		}
		return bz.Fr({
			clazz: "m-layer-w5",
			html: bg.eH("m-popup-info", {
				tip: eI,
				oktext: "马上去订购",
				cctext: "以后再说"
			}),
			onaction: function(bi) {
				if (bi.action == "ok") {
					location.dispatch2("/payfee?mvId=" + Ns);
					bfn.Dk("click", {
						type: "tobuyone",
						name: "box",
						source: "mv",
						sourceid: Ns || 0
					})
				}
			}
		})
	};
	bz.DO = function() {
		function compareFee(Nn, Ni) {
			var bQ = {
				1: 99,
				8: 99,
				4: 88,
				16: 99
			};
			return (bQ[Nn] || 0) - (bQ[Ni] || 0)
		}
		return function(bn, eI, sD, tG) {
			sD = sD || {};
			var jM = [],
				qj = {},
				DM = 0,
				DL = 0;
			if (!bn || !bn.length) return jM;
			bm.bfq(bn, function(cB) {
				var gB = bz.DH(cB);
				if (gB == 0) {
					jM.push(cB)
				} else if (gB == 10) {
					if (cB.privilege) {
						cB.fee = cB.privilege.fee
					}
					if (compareFee(cB.fee, qj.fee) > 0) {
						qj = cB
					}
				} else if (gB == 11) {
					++DM;
					if (!sD.play) jM.push(cB)
				} else if (gB == 1e3) {
					++DL;
					if (!sD.download) jM.push(cB)
				}
			});
			if (jM.length == 0 && eI) {
				if (DM == bn.length) {
					var sI = bn[0].privilege || {};
					if (sI.payed) {
						bz.su("唱片公司要求，该歌曲须下载后播放")
					} else {
						bz.sv(sI.fee, null, null, tG)
					}
				} else if (DL == bn.length) {
					bz.su("因版权方要求，该歌曲不支持下载")
				} else if (qj.id) {
					bz.sv(qj.fee, qj.id, null, tG)
				} else {
					bz.su()
				}
			}
			return jM
		}
	}();
	bz.DH = function(cB) {
		if (!cB) return 0;
		var gB = cB.privilege;
		if (cB.program) return 0;
		if (window.GAbroad) return 100;
		if (gB) {
			if (gB.fee > 0 && gB.fee != 8 && gB.payed == 0) return 10;
			if (gB.fee == 16) return 11;
			if ((gB.fee == 0 || gB.payed) && gB.pl > 0 && gB.dl == 0) return 1e3;
			if (gB.pl == 0 && gB.dl == 0) return 100;
			return 0
		} else {
			if (cB.status >= 0) return 0;
			if (cB.fee > 0) return 10;
			return 100
		}
	}
})();
(function() {
	var bh = NEJ.P,
		bE = bh("nej.j"),
		bm = bh("nej.u");
	if (window["GRef"] && window["GRef"] == "mail") {
		bE.cN = bE.cN.fR(function(bi) {
			bf = bi.args[1];
			bf.query = bf.query || {};
			if (bm.dW(bf.query)) bf.query = bm.fQ(bf.query);
			bf.query.ref = "mail"
		})
	}
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bo = bh("nej.v"),
		bg = bh("nej.e"),
		bs = bh("nej.ui"),
		bj;
	if (!!bs.sJ) return;
	var gy = bg.kN(".#<uispace>{position:absolute;background:#fff;}");
	bs.sJ = NEJ.C();
	bj = bs.sJ.cg(bs.qE);
	bj.cP = function(bf) {
		this.cS(bf);
		this.eO([
			[document, "click", this.ql.bq(this)]
		]);
		this.Mx = !!bf.nostop;
		this.DG = {
			top: bf.top,
			left: bf.left
		}
	};
	bj.dx = function() {
		delete this.DF;
		delete this.yn;
		delete this.hj;
		delete this.DC;
		delete this.sN;
		delete this.DG;
		this.dA()
	};
	bj.gj = function() {
		this.hB = gy
	};
	bj.fq = function() {
		this.gD();
		this.qy = this.bG;
		bo.bW(this.bG, "click", this.Ml.bq(this))
	};
	bj.ql = function(bi) {
		if (bi.button != 2) this.dk()
	};
	bj.Ml = function(bi) {
		if (this.Mx) return;
		bo.pk(bi);
		var bp = bo.cK(bi);
		if (bp.tagName == "A") bo.fw(bi)
	};
	bj.Mj = function() {
		var cA = /\s+/i;
		return function(gf) {
			gf = (gf || "").trim().toLowerCase().split(cA);
			gf[0] = gf[0] || "bottom";
			gf[1] = gf[1] || "left";
			this.hj = gf
		}
	}();
	bj.Mi = function(gf) {
		var bw = {},
			fU = this.yn,
			ZN = bg.ko(),
			fx = this.bG.offsetWidth,
			fW = this.bG.offsetHeight;
		switch (gf[0]) {
			case "top":
				bw.top = fU.top - fW;
				bw.left = gf[1] == "right" ? fU.left + fU.width - fx : fU.left;
				break;
			case "left":
				bw.left = fU.left - fx;
				bw.top = gf[1] == "bottom" ? fU.top + fU.height - fW : fU.top;
				break;
			case "right":
				bw.left = fU.left + fU.width;
				bw.top = gf[1] == "bottom" ? fU.top + fU.height - fW : fU.top;
				break;
			default:
				bw.top = fU.top + fU.height;
				bw.left = gf[1] == "right" ? fU.left + fU.width - fx : fU.left;
				break
		}
		return bw
	};
	bj.nr = function() {
		if (!this.DC) {
			this.li(this.DG);
			return
		}
		if (!!this.sN) {
			this.li(this.DF);
			return
		}
		if (!!this.yn) this.li(this.Mi(this.hj))
	};
	bj.Mf = function(bp, cY, bi) {
		cY = cY || bX;
		var DB = bg.ko(),
			cz = bo.oQ(bi) + (cY.left || 0),
			dZ = bo.pZ(bi) + (cY.top || 0),
			fx = bp.offsetWidth + (cY.right || 0),
			fW = bp.offsetHeight + (cY.bottom || 0),
			nw = DB.scrollWidth,
			yC = DB.scrollHeight,
			yD = cz + fx,
			yE = dZ + fW;
		switch (this.hj[0]) {
			case "top":
				dZ = yE > yC ? dZ - fW : dZ;
				if (this.hj[1] == "right") {
					cz = cz - fx < 0 ? 0 : cz - fx
				} else {
					cz = yD > nw ? nw - fx : cz
				}
				break;
			case "left":
				cz = yD > nw ? nw - fx : cz;
				if (this.hj[1] == "top") {
					dZ = yE > yC ? dZ - fW : dZ
				} else {
					dZ = dZ - fW < 0 ? dZ : dZ - fW
				}
				break;
			case "right":
				cz = cz - fx < 0 ? 0 : cz - fx;
				if (this.hj[1] == "top") {
					dZ = yE > yC ? dZ - fW : dZ
				} else {
					dZ = dZ - fW < 0 ? dZ : dZ - fW
				}
				break;
			default:
				dZ = dZ - fW < 0 ? dZ : dZ - fW;
				if (this.hj[1] == "left") {
					cz = yD > nw ? nw - fx : cz
				} else {
					cz = cz - fx < 0 ? 0 : cz - fx
				}
				break
		}
		return {
			top: dZ,
			left: cz
		}
	};
	bj.yF = function() {
		var LA = function(bp, cY) {
			bp = bg.bL(bp);
			if (!bp) return;
			cY = cY || bX;
			var cj = bg.kT(bp);
			return {
				top: cj.y - (cY.top || 0),
				left: cj.x - (cY.left || 0),
				width: bp.offsetWidth + (cY.right || 0),
				height: bp.offsetHeight + (cY.bottom || 0)
			}
		};
		return function(bf) {
			bf = bf || bX;
			this.sN = bf.event;
			this.Mj(bf.align);
			if (!!this.sN) this.DF = this.Mf(bf.target, bf.delta, this.sN);
			this.yn = LA(bf.target, bf.delta);
			this.DC = !!bf.fitable;
			this.cE();
			return this
		}
	}()
})();
(function() {
	var bh = NEJ.P,
		bX = NEJ.O,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bs = bh("nej.ui"),
		bj, cf;
	if (!!bs.nv) return;
	bs.nv = NEJ.C();
	bj = bs.nv.cg(bs.sX);
	cf = bs.nv.ff;
	bs.nv.ZR = function() {
		var Ll = function(bi, bt, dh, eJ, qt) {
			var cq, bba = bt + "-i",
				bF = dh.km,
				Dy = !!eJ.noclear,
				Dx = !!eJ.toggled;
			if (bm.es(eJ.onbeforeclick)) {
				var KR = eJ.noclear,
					KL = eJ.toggled;
				try {
					eJ.onbeforeclick(eJ)
				} catch (e) {}
				Dy = !!eJ.noclear;
				Dx = !!eJ.toggled;
				eJ.toggled = KL;
				eJ.noclear = KR
			}
			var lH = bF[bba];
			if (Dx && !!lH) {
				lH.dk();
				return
			}
			bo.ep(bi);
			if (!Dy) {
				bo.bJ(document, "click");
				cq = dh.bZ(eJ)
			} else {
				cq = dh.SV(eJ, !0)
			}
			bF[bba] = cq;
			cq.nz("onbeforerecycle", function() {
				delete bF[bba]
			});
			cq.yF(qt)
		};
		return function(bl, bf) {
			bl = bg.bL(bl);
			if (!bl) return this;
			if (!this.km) this.km = {};
			var bt = bg.gl(bl);
			if (!!this.km[bt]) return this;
			bf = NEJ.X({}, bf);
			var qt = NEJ.EX({
				align: "",
				delta: null,
				fitable: !1
			}, bf);
			qt.target = bt;
			bf.destroyable = !0;
			if (!bf.fixed) {
				qt.fitable = !0;
				bf.parent = document.body
			}
			this.km[bt] = [bt, bf.event || "click", Ll.jh(null, bt, this, bf, qt)];
			bo.bW.apply(bo, this.km[bt]);
			return this
		}
	}();
	bs.nv.ZT = function(bl) {
		if (!this.km) return this;
		var bt = bg.gl(bl),
			bi = this.km[bt];
		if (!bi) return this;
		delete this.km[bt];
		bo.jB.apply(bo, bi);
		var cq = this.km[bt + "-i"];
		if (!!cq) cq.dk();
		return this
	};
	bj.wg = function() {
		return bs.sJ.bZ(this.eh)
	};
	bj.qo = function() {
		cf.qo.apply(this, arguments);
		this.eh.top = null;
		this.eh.left = null;
		this.eh.nostop = !1
	};
	bj.yF = function(bf) {
		if (!!this.jW) this.jW.yF(bf);
		return this
	}
})();
(function() {
	var bh = NEJ.P,
		cQ = bh("nej.ui"),
		bV = bh("nm.l"),
		bj, cf;
	bV.yS = NEJ.C();
	bj = bV.yS.cg(cQ.nv);
	bj.cP = function(bf) {
		bf.nohack = true;
		this.cS(bf)
	}
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bV = bh("nm.l"),
		bj, cf;
	bV.eE = NEJ.C();
	bj = bV.eE.cg(bV.yS);
	cf = bV.eE.ff;
	bj.cP = function(bf) {
		this.cS(bf);
		this.jL = bf.type || 1
	};
	bj.fq = function() {
		this.gD();
		this.bG = bg.nt(this.XR());
		var bn = bg.gL(this.bG);
		this.Dw = bn[0];
		this.iu = bn[1]
	};
	bj.XR = function() {
		return '<div class="sysmsg"><i class="u-icn u-icn-31"></i><span></span></div>'
	};
	bj.nr = function() {
		var bA = {},
			eR = this.bG.style,
			bM = bg.ko(),
			iQ = {
				left: bM.scrollLeft,
				top: bM.scrollTop
			},
			cY = {
				left: bM.clientWidth - this.bG.offsetWidth,
				top: bM.clientHeight - this.bG.offsetHeight
			};
		bA.top = Math.max(0, iQ.top + cY.top / 2);
		bA.left = Math.max(0, iQ.left + cY.left / 2);
		this.jW.li(bA)
	};
	bj.cE = function(bf) {
		cf.cE.call(this);
		this.nr();
		this.jL == 1 ? bg.rB(this.Dw, "u-icn-32", "u-icn-31") : bg.rB(this.Dw, "u-icn-31", "u-icn-32");
		this.iu.innerHTML = bf.tip || ""
	};
	bV.eE.cE = function() {
		var bbh;
		return function(bf) {
			clearTimeout(bbh);
			if (bf.parent === undefined) bf.parent = document.body;
			if (bf.autoclose === undefined) bf.autoclose = true;
			bf.clazz = "m-sysmsg";
			!!this.eG && this.eG.cF();
			this.eG = this.bZ(bf);
			this.eG.cE(bf);
			if (bf.autoclose) bbh = setTimeout(this.dk.bq(this), 2e3)
		}
	}();
	bV.eE.dk = function() {
		!!this.eG && this.eG.dk()
	}
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		fz = NEJ.R,
		bR = bh("nej.ut"),
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		bE = bh("nej.j"),
		bD = bh("nm.d"),
		bV = bh("nm.l"),
		bba = "playlist-tracks_",
		bj;
	bD.hm({
		"playlist_my-list": {
			url: "/api/user/playlist",
			type: "GET",
			format: function(bN, bf) {
				this.Xt(bN.playlist);
				return {
					total: 0,
					list: fz
				}
			},
			onerror: function() {
				this.bJ("onlisterror")
			}
		},
		"playlist_new-add": {
			url: "/api/playlist/create",
			format: function(bN, bf) {
				var fS = bN.playlist;
				fS.creator = GUser;
				fS.isHost = !0;
				fS.typeFlag = "playlist";
				return fS
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.hl, "listchange", bi)
			},
			onmessage: function() {
				var hE = {
					507: "歌单数量超过上限！",
					405: "你操作太快了，请休息一会再试！",
					406: "你操作太快了，请休息一会再试！"
				};
				return function(fE) {
					bV.eE.cE({
						tip: hE[fE] || "创建失败",
						type: 2
					})
				}
			}()
		},
		"playlist_new-del": {
			url: "/api/playlist/delete",
			type: "GET",
			filter: function(bf) {
				bf.id = bf.data.pid
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.hl, "listchange", bi)
			},
			onmessage: function() {
				var hE = {
					404: "歌单不存在！",
					405: "你操作太快了，请休息一会再试！",
					406: "你操作太快了，请休息一会再试！"
				};
				return function(fE) {
					bV.eE.cE({
						tip: hE[fE] || "删除失败",
						type: 2
					})
				}
			}()
		},
		"playlist_fav-add": {
			type: "GET",
			url: "/api/playlist/subscribe/",
			filter: function(bf) {
				var tG = bf.ext || {};
				bf.ext = NEJ.X(tG, bf.data);
				bf.data = {
					id: bf.ext.id
				}
			},
			format: function(bN, bf) {
				bV.eE.cE({
					tip: "收藏成功" + (bN.point > 0 ? ' 获得<em class="s-fc6">' + bN.point + "积分</em>" : "")
				});
				var bfp = bf.ext;
				bfp.subscribedCount++;
				return bfp
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.Wm, "listchange", bi);
				bo.bJ(bD.Wm, "itemchange", {
					attr: "subscribedCount",
					data: bi.data
				})
			},
			onmessage: function() {
				var hE = {
					404: "歌单不存在！",
					501: "歌单已经收藏！",
					506: "歌单收藏数量超过上限！"
				};
				return function(fE) {
					bV.eE.cE({
						type: 2,
						tip: hE[fE] || "收藏失败，请稍后再试！"
					})
				}
			}()
		},
		"playlist_fav-del": {
			url: "/api/playlist/unsubscribe",
			type: "GET",
			filter: function(bf) {
				bf.id = bf.data.id = bf.data.pid
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.hl, "listchange", bi)
			},
			onmessage: function() {
				var hE = {
					404: "歌单不存在！",
					405: "你操作太快了，请休息一会再试！",
					406: "你操作太快了，请休息一会再试！"
				};
				return function(fE) {
					bV.eE.cE({
						tip: hE[fE],
						type: 2
					})
				}
			}()
		},
		"playlist_new-update": {
			url: ["playlist-update-name", "playlist-update-tag", "playlist-update-desc"],
			filter: function(bf) {
				var bk = bf.data,
					ty = {};
				ty["playlist-update-name"] = {
					id: bk.id,
					name: bk.name
				};
				ty["playlist-update-tag"] = {
					id: bk.id,
					tags: bk.tags.join(";")
				};
				ty["playlist-update-desc"] = {
					id: bk.id,
					desc: bk.description
				};
				bf.data = ty;
				bf.ext = bk
			},
			format: function(bB, Dq, qC, bf) {
				if (bB.code == 200 && Dq.code == 200 && qC.code == 200) {
					bf.ext.allSuccess = true;
					bV.eE.cE({
						tip: "保存成功"
					})
				} else if (bB.code == 407 || Dq.code == 407 || qC.code == 407) {
					bf.ext.allSuccess = false;
					bV.eE.cE({
						type: 2,
						tip: "输入内容包含关键字"
					})
				} else {
					bf.ext.allSuccess = false;
					bV.eE.cE({
						type: 2,
						tip: "保存失败"
					})
				}
				return this.eW(bf.ext.id)
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.hl, "listchange", bi)
			},
			onmessage: function(fE) {
				bV.eE.cE({
					type: 2,
					tip: "保存失败"
				})
			}
		},
		"playlist-update-name": {
			url: "/api/playlist/update/name",
			format: function(bN, bf) {
				var bfp = this.eW(bf.ext.id);
				if (bN.code == 200) bfp.name = bf.ext.name;
				return bN
			}
		},
		"playlist-update-tag": {
			url: "/api/playlist/tags/update",
			format: function(bN, bf) {
				var bfp = this.eW(bf.ext.id);
				if (bN.code == 200) bfp.tags = bf.ext.tags;
				return bN
			}
		},
		"playlist-update-desc": {
			url: "/api/playlist/desc/update",
			format: function(bN, bf) {
				var bfp = this.eW(bf.ext.id);
				if (bN.code == 200) bfp.description = bf.ext.description;
				return bN
			}
		},
		"playlist-update-cover": {
			url: "/api/playlist/cover/update",
			filter: function(bf) {
				bf.url = bf.data.url;
				delete bf.data.url
			},
			format: function(bN, bf) {
				bV.eE.cE({
					tip: "保存成功"
				});
				var bfp = this.eW(bf.data.id);
				bfp.coverImgUrl = bf.url;
				bf.ext = bfp;
				return bfp
			},
			onmessage: function(fE) {
				bV.eE.cE({
					type: 2,
					tip: "保存失败"
				})
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.hl, "itemchange", {
					attr: "coverImgUrl",
					data: bf.ext
				})
			}
		},
		"playlist-upcount": {
			url: "/api/playlist/update/playcount",
			type: "GET",
			format: function(bN, bf) {
				var fS = this.eW(bf.data.id);
				if (!fS) return;
				fS.playCount++;
				bo.bJ(bD.hl, "itemchange", {
					attr: "playcount",
					data: fS
				})
			}
		}
	});
	bD.hl = NEJ.C();
	bj = bD.hl.cg(bD.gY);
	bj.cI = function() {
		this.dv()
	};
	bj.Wi = function() {
		var eS = GUser.userId;
		this.jC({
			limit: 1001,
			key: "playlist_my-" + eS,
			data: {
				offset: 0,
				limit: 1001,
				uid: eS
			}
		})
	};
	bj.Xt = function(bn) {
		var eS = GUser.userId,
			Dp = [],
			Dn = [];
		bm.bfq(bn, function(bfp) {
			bfp.typeFlag = "playlist";
			if (bfp.creator && bfp.creator.userId == eS) {
				if (bfp.specialType == 5) bfp.name = "我喜欢的音乐";
				bfp.isHost = !0;
				Dp.push(bfp)
			} else {
				Dn.push(bfp)
			}
		});
		this.mP("playlist_new-" + eS, Dp);
		this.mP("playlist_fav-" + eS, Dn)
	};
	bj.baa = function(bk) {
		this.dT("playlist-update-cover", {
			data: bk
		})
	};
	bj.bab = function() {
		var qF = this.VA.bL("host-plist");
		if (qF.length == 0) {
			return
		}
		if (qF.length == 1 && qF[0].trackCount <= 0) {
			return
		}
		for (var i = 0, len = qF.length; i < len; i++) {
			var bfp = qF[i];
			if (bfp.trackCount > 0) return bfp.id
		}
		return this.VA.bL("host-plist")[0].id
	};
	bj.Vv = function(bt) {
		if (GUser && GUser.userId > 0) {
			this.dT("playlist-upcount", {
				data: {
					id: bt
				}
			})
		}
	};
	bj.bac = function() {
		if (GUser && GUser.userId > 0) {
			return !0
		} else {
			top.login();
			return !1
		}
	};
	bj.bad = function() {
		return GUser.userId
	};
	bj.zx = function(bfp) {
		if (bfp.userId == GUser.userId && bfp.specialType == 5) bfp.name = "我喜欢的音乐";
		bo.bJ(this.constructor, "itemchange", {
			data: this.kP(bfp)
		});
		return bfp
	};
	bR.hk.bZ({
		element: bD.hl,
		event: ["listchange", "playcountchange", "itemchange"]
	})
})();
(function() {
	var bh = NEJ.P,
		fz = NEJ.R,
		bY = NEJ.F,
		bX = NEJ.O,
		bR = bh("nej.ut"),
		bo = bh("nej.v"),
		bm = bh("nej.u"),
		bz = bh("nm.x"),
		bD = bh("nm.d"),
		bV = bh("nm.l"),
		bj, cf;
	bD.hm({
		"program-get": {
			url: "/api/dj/program/detail",
			format: function(bN) {
				return bN.program
			}
		},
		"program_djradio-list": {
			type: "GET",
			url: "/api/dj/program/byradio",
			filter: function(bf) {
				bf.data.limit = 1e3;
				delete bf.data.id
			},
			format: function(bN, bf) {
				return bN.programs
			}
		},
		"program_fav-list": {
			url: "/api/djprogram/subscribed/paged/",
			format: function(bN, bf) {
				return bN.programs
			},
			onerror: "onlisterror"
		},
		"program_fav-add": {
			url: "/api/djprogram/subscribe/",
			filter: function(bf) {
				bf.ext = bf.data;
				bf.data = {
					id: bf.ext.id
				};
				bf.id = bf.data.id
			},
			format: function(bN, bf) {
				bV.eE.cE({
					tip: "收藏成功"
				});
				var bfp = bf.ext;
				bfp.subscribedCount++;
				bfp.subscribed = !0;
				return bfp
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.jn, "listchange", bi)
			},
			onmessage: function() {
				var hE = {
					404: "节目不存在！",
					501: "节目已收藏！"
				};
				return function(fE) {
					bV.eE.cE({
						type: 2,
						tip: hE[fE] || "收藏失败！"
					})
				}
			}()
		},
		"program_fav-del": {
			url: "/api/djprogram/unsubscribe/",
			finaly: function(bi, bf) {
				bo.bJ(bD.jn, "listchange", bi)
			},
			onmessage: function() {
				var hE = {
					404: "节目不存在！",
					502: "没有收藏此节目！"
				};
				return function(fE) {
					bz.QK({
						txt: hE[fE] || "取消收藏失败！"
					})
				}
			}()
		},
		"program-update-count": {
			type: "GET",
			url: "/api/dj/program/listen",
			filter: function(bf) {
				var bfp = this.eW(bf.data.id) || bX;
				bf.ext = (bfp.listenerCount || 0) + 1
			},
			format: function(bN, bf) {
				var bfp = this.eW(bf.data.id);
				if (!!bfp) {
					bfp.listenerCount = Math.max(bf.ext, bfp.listenerCount || 0)
				}
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.jn, "itemchange", {
					attr: "playCount",
					data: this.eW(bf.data.id)
				})
			}
		},
		"program-like": {
			url: "/api/resource/like",
			filter: function(bf) {
				bf.data = {
					threadId: "A_DJ_1_" + bf.id
				}
			},
			format: function(bN, bf) {
				var bfp = bf.ext.data || this.eW(bf.id);
				bfp.liked = true;
				bfp.likedCount++;
				bf.ext.data = bfp;
				try {
					top.player.setLike(bfp)
				} catch (e) {}
				return bfp
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.jn, "itemchange", {
					attr: "likedCount",
					data: bf.ext.data
				})
			}
		},
		"program-unlike": {
			url: "/api/resource/unlike",
			filter: function(bf) {
				bf.data = {
					threadId: "A_DJ_1_" + bf.id
				}
			},
			format: function(bN, bf) {
				var bfp = bf.ext.data || this.eW(bf.id);
				bfp.liked = false;
				bfp.likedCount--;
				bf.ext.data = bfp;
				try {
					top.player.setLike(bfp)
				} catch (e) {}
				return bfp
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.jn, "itemchange", {
					attr: "likedCount",
					data: bf.ext.data
				})
			}
		}
	});
	bD.jn = NEJ.C();
	bj = bD.jn.cg(bD.gY);
	bj.bak = function() {
		var eS = GUser.userId;
		this.jC({
			limit: 1001,
			key: "program_fav-" + eS,
			data: {
				offset: 0,
				limit: 1e3,
				uid: eS
			}
		})
	};
	bj.bal = function(fL) {
		var iU = fL[this.ju];
		bz.PG(4, function(bF) {
			bF.mP("track_program-" + iU, fL.songs)
		});
		delete fL.songs;
		var dD = fL.mainSong;
		bz.PG(4, function(bF) {
			bF.mP("track_program_main-" + iU, !dD ? [] : [dD])
		});
		fL.mainSong = (dD || bX).id
	};
	bj.bam = function(bt) {
		var fL = this.eW(bt),
			eS = localCache.vy("host.profile.userId");
		return !!fL && fL.dj.userId == eS
	};
	bj.ban = function(bt) {
		return !1
	};
	bj.zx = function(bfp) {
		bo.bJ(this.constructor, "itemchange", {
			attr: "detail",
			data: this.kP(bfp)
		});
		return bfp
	};
	bj.Vv = function(bt) {
		this.dT("program-update-count", {
			data: {
				id: bt
			}
		})
	};
	bz.bao = function(bf) {
		var bF = bD.jn.bZ({
			onitemadd: function() {
				(bf.onsuccess || bY)()
			},
			onerror: function() {
				(bf.onerror || bY)()
			}
		});
		if (bf.data.liked) {
			bF.Pp(bf.data)
		} else {
			bF.OW(bf.data)
		}
	};
	bj.OW = function(fL) {
		if (!bz.CL()) return;
		var fj = {
			ext: {}
		};
		if (bm.jq(fL)) {
			fj.id = fL.id;
			fj.ext.data = fL
		} else {
			fj.id = fL
		}
		this.dT("program-like", fj)
	};
	bj.Pp = function(fL) {
		if (!bz.CL()) return;
		var fj = {
			ext: {}
		};
		if (bm.jq(fL)) {
			fj.id = fL.id;
			fj.ext.data = fL
		} else {
			fj.id = fL
		}
		this.dT("program-unlike", fj)
	};
	bR.hk.bZ({
		element: bD.jn,
		event: ["listchange", "itemchange"]
	})
})();
(function() {
	var bh = NEJ.P,
		bY = NEJ.F,
		fz = NEJ.R,
		bR = bh("nej.ut"),
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		bE = bh("nej.j"),
		bD = bh("nm.d"),
		bV = bh("nm.l"),
		bz = bh("nm.x"),
		bba = "playlist-tracks_",
		bz = bh("nm.x"),
		bj;
	bD.hm({
		"track-get": {
			url: "/api/v3/song/detail",
			filter: function(bf) {
				bf.data.c = JSON.stringify([{
					id: bf.data.id
				}])
			},
			format: function(bN, bf) {
				var cB = bz.qW(bN.songs[0]);
				cB.privilege = bN.privileges[0];
				return cB
			}
		},
		"track_playlist-list": {
			url: "/api/v3/playlist/detail",
			filter: function(bf) {
				bf.data.n = 1e3
			},
			format: function(bN, bf) {
				var nh = [];
				this.qX.zx(bN.playlist);
				bm.bfq(bN.playlist.tracks, function(dD, bfo) {
					var CJ = bz.qW(dD);
					CJ.privilege = bN.privileges[bfo];
					nh.push(CJ)
				}, this);
				return nh
			}
		},
		"track_album-list": {
			url: "/api/v1/album/{id}",
			format: function(bN, bf) {
				var nh = [];
				bm.bfq(bN.songs, function(cB) {
					nh.push(bz.qW(cB))
				});
				return nh
			}
		},
		"track_playlist-add": {
			url: "/api/playlist/manipulate/tracks",
			filter: function(bf) {
				var bQ = {},
					bk = bf.data,
					NK = this.gK(bf.key) || [];
				Ao = [];
				bm.bfq(NK, function(dD) {
					var bt = bm.jq(dD) ? dD.id : dD;
					bQ[bt] = true
				});
				bf.ext = [];
				bm.bfq(bk.tracks, function(dD) {
					var bt = bm.jq(dD) ? dD.id : dD;
					if (!bQ[bt]) {
						Ao.push(bt);
						bQ[bt] = true;
						bf.ext.push(dD)
					}
				});
				bk.trackIds = JSON.stringify(Ao);
				bk.op = "add";
				if (!Ao.length) {
					bf.value = {
						code: 502
					}
				}
			},
			format: function(bN, bf) {
				bV.eE.cE({
					tip: "已添加至歌单"
				});
				var fS = this.qX.eW(bf.data.pid);
				if (!!bN.coverImgUrl) fS.coverImgUrl = bN.coverImgUrl;
				bm.gU(bf.ext, function(dD) {
					this.En(bf, bm.jq(dD) ? dD : null);
					if (!!fS) fS.trackCount++
				}, this);
				bo.bJ(bD.hl, "itemchange", {
					data: fS || {},
					cmd: "add"
				});
				this.bJ("onaddsuccess");
				return null
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.lB, "listchange", {
					key: bf.key,
					action: "refresh"
				});
				var iU = bf.data.pid,
					cR = this.jE(bf.key)
			},
			onmessage: function() {
				var hE = {
					502: "歌曲已存在！"
				};
				return function(fE) {
					setTimeout(function() {
						bV.eE.cE({
							tip: hE[fE] || "添加失败，请稍后再试！",
							type: 2
						})
					}, 0)
				}
			}()
		},
		"track_playlist-del": {
			url: "/api/playlist/manipulate/tracks",
			filter: function(bf) {
				var bk = bf.data;
				bf.ext = bk.trackIds;
				bk.trackIds = JSON.stringify(bk.trackIds);
				bk.op = "del"
			},
			format: function(bN, bf) {
				var fS = this.qX.eW(bf.data.pid);
				bm.bfq(bf.ext, function(bt) {
					this.Ej({
						id: bt,
						key: "track_playlist-" + bf.data.pid
					}, !0);
					if (!!fS) fS.trackCount = Math.max(0, fS.trackCount - 1)
				}, this);
				bo.bJ(bD.hl, "itemchange", {
					data: fS || {},
					cmd: "del"
				});
				return null
			},
			finaly: function(bi, bf) {
				bo.bJ(bD.lB, "listchange", {
					key: bf.key,
					action: "refresh"
				})
			},
			onmessage: function(fE) {
				bz.QK({
					text: "歌曲删除失败！"
				})
			}
		},
		"track_program-list": {
			url: "/api/dj/program/detail",
			format: function(bN, bf) {
				return this.MV.zx(bN.program).songs
			},
			onerror: "onlisterror"
		},
		"track_listen_record-list": {
			url: "/api/v1/play/record",
			format: function(bN, bf) {
				var bn = [];
				if (bf.data.type == -1) {
					if (bN.weekData && bN.weekData.length) {
						bf.data.type = 1
					} else {
						bf.data.type = 0
					}
				}
				if (bf.data.type == 1) {
					bn = this.CG(bN.weekData)
				} else {
					bn = this.CG(bN.allData)
				}
				return bn
			},
			onerror: "onlisterror"
		},
		"track_day-list": {
			url: "/api/v1/discovery/recommend/songs",
			format: function(bN) {
				var hD = [];
				bm.bfq(bN.recommend, function(cB, bfo) {
					hD.push({
						action: "recommendimpress",
						json: JSON.stringify({
							alg: cB.alg,
							scene: "user-song",
							position: bfo,
							id: cB.id
						})
					})
				});
				this.qZ.TJ(hD);
				return bN.recommend
			},
			onerror: "onlisterror"
		}
	});
	bD.lB = NEJ.C();
	bj = bD.lB.cg(bD.gY);
	bj.cI = function() {
		this.dv();
		this.qX = bD.hl.bZ();
		this.MV = bD.jn.bZ();
		this.qZ = bD.qI.bZ()
	};
	bj.CG = function(bn) {
		var bw = [],
			gA = 0;
		bm.bfq(bn, function(bfp, bfo) {
			var cB = bz.qW(bfp.song);
			if (bfo == 0) {
				gA = bfp.score
			}
			cB.max = gA;
			cB.playCount = bfp.playCount;
			cB.score = bfp.score;
			bw.push(cB)
		});
		return bw
	};
	bR.hk.bZ({
		element: bD.lB,
		event: ["listchange"]
	})
})();
(function() {
	var bh = NEJ.P,
		dJ = bh("nej.g"),
		bE = bh("nej.j"),
		bm = bh("nej.u"),
		rf = bh("nm.x.ek");
	rf.emj = {
		"色": "00e0b",
		"流感": "509f6",
		"这边": "259df",
		"弱": "8642d",
		"嘴唇": "bc356",
		"亲": "62901",
		"开心": "477df",
		"呲牙": "22677",
		"憨笑": "ec152",
		"猫": "b5ff6",
		"皱眉": "8ace6",
		"幽灵": "15bb7",
		"蛋糕": "b7251",
		"发怒": "52b3a",
		"大哭": "b17a8",
		"兔子": "76aea",
		"星星": "8a5aa",
		"钟情": "76d2e",
		"牵手": "41762",
		"公鸡": "9ec4e",
		"爱意": "e341f",
		"禁止": "56135",
		"狗": "fccf6",
		"亲亲": "95280",
		"叉": "104e0",
		"礼物": "312ec",
		"晕": "bda92",
		"呆": "557c9",
		"生病": "38701",
		"钻石": "14af6",
		"拜": "c9d05",
		"怒": "c4f7f",
		"示爱": "0c368",
		"汗": "5b7a4",
		"小鸡": "6bee2",
		"痛苦": "55932",
		"撇嘴": "575cc",
		"惶恐": "e10b4",
		"口罩": "24d81",
		"吐舌": "3cfe4",
		"心碎": "875d3",
		"生气": "e8204",
		"可爱": "7b97d",
		"鬼脸": "def52",
		"跳舞": "741d5",
		"男孩": "46b8e",
		"奸笑": "289dc",
		"猪": "6935b",
		"圈": "3ece0",
		"便便": "462db",
		"外星": "0a22b",
		"圣诞": "8e7",
		"流泪": "01000",
		"强": "1",
		"爱心": "0CoJU",
		"女孩": "m6Qyw",
		"惊恐": "8W8ju",
		"大笑": "d"
	};
	rf.md = ["色", "流感", "这边", "弱", "嘴唇", "亲", "开心", "呲牙", "憨笑", "猫", "皱眉", "幽灵", "蛋糕", "发怒", "大哭", "兔子", "星星", "钟情", "牵手", "公鸡", "爱意", "禁止", "狗", "亲亲", "叉", "礼物", "晕", "呆", "生病", "钻石", "拜", "怒", "示爱", "汗", "小鸡", "痛苦", "撇嘴", "惶恐", "口罩", "吐舌", "心碎", "生气", "可爱", "鬼脸", "跳舞", "男孩", "奸笑", "猪", "圈", "便便", "外星", "圣诞"]
})();
var CryptoJS = CryptoJS || function(u, p) {
	var d = {},
		l = d.lib = {},
		s = function() {},
		t = l.Base = {
			extend: function(a) {
				s.prototype = this;
				var c = new s;
				a && c.mixIn(a);
				c.hasOwnProperty("init") || (c.init = function() {
					c.$super.init.apply(this, arguments)
				});
				c.init.prototype = c;
				c.$super = this;
				return c
			},
			create: function() {
				var a = this.extend();
				a.init.apply(a, arguments);
				return a
			},
			init: function() {},
			mixIn: function(a) {
				for (var c in a) a.hasOwnProperty(c) && (this[c] = a[c]);
				a.hasOwnProperty("toString") && (this.toString = a.toString)
			},
			clone: function() {
				return this.init.prototype.extend(this)
			}
		},
		r = l.WordArray = t.extend({
			init: function(a, c) {
				a = this.words = a || [];
				this.sigBytes = c != p ? c : 4 * a.length
			},
			toString: function(a) {
				return (a || v).stringify(this)
			},
			concat: function(a) {
				var c = this.words,
					e = a.words,
					j = this.sigBytes;
				a = a.sigBytes;
				this.clamp();
				if (j % 4)
					for (var k = 0; k < a; k++) c[j + k >>> 2] |= (e[k >>> 2] >>> 24 - 8 * (k % 4) & 255) << 24 - 8 * ((j + k) % 4);
				else if (65535 < e.length)
					for (k = 0; k < a; k += 4) c[j + k >>> 2] = e[k >>> 2];
				else c.push.apply(c, e);
				this.sigBytes += a;
				return this
			},
			clamp: function() {
				var a = this.words,
					c = this.sigBytes;
				a[c >>> 2] &= 4294967295 << 32 - 8 * (c % 4);
				a.length = u.ceil(c / 4)
			},
			clone: function() {
				var a = t.clone.call(this);
				a.words = this.words.slice(0);
				return a
			},
			random: function(a) {
				for (var c = [], e = 0; e < a; e += 4) c.push(4294967296 * u.random() | 0);
				return new r.init(c, a)
			}
		}),
		w = d.enc = {},
		v = w.Hex = {
			stringify: function(a) {
				var c = a.words;
				a = a.sigBytes;
				for (var e = [], j = 0; j < a; j++) {
					var k = c[j >>> 2] >>> 24 - 8 * (j % 4) & 255;
					e.push((k >>> 4).toString(16));
					e.push((k & 15).toString(16))
				}
				return e.join("")
			},
			parse: function(a) {
				for (var c = a.length, e = [], j = 0; j < c; j += 2) e[j >>> 3] |= parseInt(a.substr(j, 2), 16) << 24 - 4 * (j % 8);
				return new r.init(e, c / 2)
			}
		},
		b = w.Latin1 = {
			stringify: function(a) {
				var c = a.words;
				a = a.sigBytes;
				for (var e = [], j = 0; j < a; j++) e.push(String.fromCharCode(c[j >>> 2] >>> 24 - 8 * (j % 4) & 255));
				return e.join("")
			},
			parse: function(a) {
				for (var c = a.length, e = [], j = 0; j < c; j++) e[j >>> 2] |= (a.charCodeAt(j) & 255) << 24 - 8 * (j % 4);
				return new r.init(e, c)
			}
		},
		x = w.Utf8 = {
			stringify: function(a) {
				try {
					return decodeURIComponent(escape(b.stringify(a)))
				} catch (c) {
					throw Error("Malformed UTF-8 data")
				}
			},
			parse: function(a) {
				return b.parse(unescape(encodeURIComponent(a)))
			}
		},
		q = l.BufferedBlockAlgorithm = t.extend({
			reset: function() {
				this.bk = new r.init;
				this.CD = 0
			},
			uu: function(a) {
				"string" == typeof a && (a = x.parse(a));
				this.bk.concat(a);
				this.CD += a.sigBytes
			},
			ng: function(a) {
				var c = this.bk,
					e = c.words,
					j = c.sigBytes,
					k = this.blockSize,
					b = j / (4 * k),
					b = a ? u.ceil(b) : u.max((b | 0) - this.CE, 0);
				a = b * k;
				j = u.min(4 * a, j);
				if (a) {
					for (var q = 0; q < a; q += k) this.CC(e, q);
					q = e.splice(0, a);
					c.sigBytes -= j
				}
				return new r.init(q, j)
			},
			clone: function() {
				var a = t.clone.call(this);
				a.bk = this.bk.clone();
				return a
			},
			CE: 0
		});
	l.Hasher = q.extend({
		cfg: t.extend(),
		init: function(a) {
			this.cfg = this.cfg.extend(a);
			this.reset()
		},
		reset: function() {
			q.reset.call(this);
			this.AB()
		},
		update: function(a) {
			this.uu(a);
			this.ng();
			return this
		},
		finalize: function(a) {
			a && this.uu(a);
			return this.uA()
		},
		blockSize: 16,
		Az: function(a) {
			return function(b, e) {
				return (new a.init(e)).finalize(b)
			}
		},
		Mr: function(a) {
			return function(b, e) {
				return (new n.HMAC.init(a, e)).finalize(b)
			}
		}
	});
	var n = d.algo = {};
	return d
}(Math);
(function() {
	var u = CryptoJS,
		p = u.lib.WordArray;
	u.enc.Base64 = {
		stringify: function(d) {
			var l = d.words,
				p = d.sigBytes,
				t = this.bQ;
			d.clamp();
			d = [];
			for (var r = 0; r < p; r += 3)
				for (var w = (l[r >>> 2] >>> 24 - 8 * (r % 4) & 255) << 16 | (l[r + 1 >>> 2] >>> 24 - 8 * ((r + 1) % 4) & 255) << 8 | l[r + 2 >>> 2] >>> 24 - 8 * ((r + 2) % 4) & 255, v = 0; 4 > v && r + .75 * v < p; v++) d.push(t.charAt(w >>> 6 * (3 - v) & 63));
			if (l = t.charAt(64))
				for (; d.length % 4;) d.push(l);
			return d.join("")
		},
		parse: function(d) {
			var l = d.length,
				s = this.bQ,
				t = s.charAt(64);
			t && (t = d.indexOf(t), -1 != t && (l = t));
			for (var t = [], r = 0, w = 0; w < l; w++)
				if (w % 4) {
					var v = s.indexOf(d.charAt(w - 1)) << 2 * (w % 4),
						b = s.indexOf(d.charAt(w)) >>> 6 - 2 * (w % 4);
					t[r >>> 2] |= (v | b) << 24 - 8 * (r % 4);
					r++
				}
			return p.create(t, r)
		},
		bQ: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
	}
})();
(function(u) {
	function p(b, n, a, c, e, j, k) {
		b = b + (n & a | ~n & c) + e + k;
		return (b << j | b >>> 32 - j) + n
	}

	function d(b, n, a, c, e, j, k) {
		b = b + (n & c | a & ~c) + e + k;
		return (b << j | b >>> 32 - j) + n
	}

	function l(b, n, a, c, e, j, k) {
		b = b + (n ^ a ^ c) + e + k;
		return (b << j | b >>> 32 - j) + n
	}

	function s(b, n, a, c, e, j, k) {
		b = b + (a ^ (n | ~c)) + e + k;
		return (b << j | b >>> 32 - j) + n
	}
	for (var t = CryptoJS, r = t.lib, w = r.WordArray, v = r.Hasher, r = t.algo, b = [], x = 0; 64 > x; x++) b[x] = 4294967296 * u.abs(u.sin(x + 1)) | 0;
	r = r.MD5 = v.extend({
		AB: function() {
			this.eo = new w.init([1732584193, 4023233417, 2562383102, 271733878])
		},
		CC: function(q, n) {
			for (var a = 0; 16 > a; a++) {
				var c = n + a,
					e = q[c];
				q[c] = (e << 8 | e >>> 24) & 16711935 | (e << 24 | e >>> 8) & 4278255360
			}
			var a = this.eo.words,
				c = q[n + 0],
				e = q[n + 1],
				j = q[n + 2],
				k = q[n + 3],
				z = q[n + 4],
				r = q[n + 5],
				t = q[n + 6],
				w = q[n + 7],
				v = q[n + 8],
				A = q[n + 9],
				B = q[n + 10],
				C = q[n + 11],
				u = q[n + 12],
				D = q[n + 13],
				E = q[n + 14],
				x = q[n + 15],
				f = a[0],
				m = a[1],
				g = a[2],
				h = a[3],
				f = p(f, m, g, h, c, 7, b[0]),
				h = p(h, f, m, g, e, 12, b[1]),
				g = p(g, h, f, m, j, 17, b[2]),
				m = p(m, g, h, f, k, 22, b[3]),
				f = p(f, m, g, h, z, 7, b[4]),
				h = p(h, f, m, g, r, 12, b[5]),
				g = p(g, h, f, m, t, 17, b[6]),
				m = p(m, g, h, f, w, 22, b[7]),
				f = p(f, m, g, h, v, 7, b[8]),
				h = p(h, f, m, g, A, 12, b[9]),
				g = p(g, h, f, m, B, 17, b[10]),
				m = p(m, g, h, f, C, 22, b[11]),
				f = p(f, m, g, h, u, 7, b[12]),
				h = p(h, f, m, g, D, 12, b[13]),
				g = p(g, h, f, m, E, 17, b[14]),
				m = p(m, g, h, f, x, 22, b[15]),
				f = d(f, m, g, h, e, 5, b[16]),
				h = d(h, f, m, g, t, 9, b[17]),
				g = d(g, h, f, m, C, 14, b[18]),
				m = d(m, g, h, f, c, 20, b[19]),
				f = d(f, m, g, h, r, 5, b[20]),
				h = d(h, f, m, g, B, 9, b[21]),
				g = d(g, h, f, m, x, 14, b[22]),
				m = d(m, g, h, f, z, 20, b[23]),
				f = d(f, m, g, h, A, 5, b[24]),
				h = d(h, f, m, g, E, 9, b[25]),
				g = d(g, h, f, m, k, 14, b[26]),
				m = d(m, g, h, f, v, 20, b[27]),
				f = d(f, m, g, h, D, 5, b[28]),
				h = d(h, f, m, g, j, 9, b[29]),
				g = d(g, h, f, m, w, 14, b[30]),
				m = d(m, g, h, f, u, 20, b[31]),
				f = l(f, m, g, h, r, 4, b[32]),
				h = l(h, f, m, g, v, 11, b[33]),
				g = l(g, h, f, m, C, 16, b[34]),
				m = l(m, g, h, f, E, 23, b[35]),
				f = l(f, m, g, h, e, 4, b[36]),
				h = l(h, f, m, g, z, 11, b[37]),
				g = l(g, h, f, m, w, 16, b[38]),
				m = l(m, g, h, f, B, 23, b[39]),
				f = l(f, m, g, h, D, 4, b[40]),
				h = l(h, f, m, g, c, 11, b[41]),
				g = l(g, h, f, m, k, 16, b[42]),
				m = l(m, g, h, f, t, 23, b[43]),
				f = l(f, m, g, h, A, 4, b[44]),
				h = l(h, f, m, g, u, 11, b[45]),
				g = l(g, h, f, m, x, 16, b[46]),
				m = l(m, g, h, f, j, 23, b[47]),
				f = s(f, m, g, h, c, 6, b[48]),
				h = s(h, f, m, g, w, 10, b[49]),
				g = s(g, h, f, m, E, 15, b[50]),
				m = s(m, g, h, f, r, 21, b[51]),
				f = s(f, m, g, h, u, 6, b[52]),
				h = s(h, f, m, g, k, 10, b[53]),
				g = s(g, h, f, m, B, 15, b[54]),
				m = s(m, g, h, f, e, 21, b[55]),
				f = s(f, m, g, h, v, 6, b[56]),
				h = s(h, f, m, g, x, 10, b[57]),
				g = s(g, h, f, m, t, 15, b[58]),
				m = s(m, g, h, f, D, 21, b[59]),
				f = s(f, m, g, h, z, 6, b[60]),
				h = s(h, f, m, g, C, 10, b[61]),
				g = s(g, h, f, m, j, 15, b[62]),
				m = s(m, g, h, f, A, 21, b[63]);
			a[0] = a[0] + f | 0;
			a[1] = a[1] + m | 0;
			a[2] = a[2] + g | 0;
			a[3] = a[3] + h | 0
		},
		uA: function() {
			var b = this.bk,
				n = b.words,
				a = 8 * this.CD,
				c = 8 * b.sigBytes;
			n[c >>> 5] |= 128 << 24 - c % 32;
			var e = u.floor(a / 4294967296);
			n[(c + 64 >>> 9 << 4) + 15] = (e << 8 | e >>> 24) & 16711935 | (e << 24 | e >>> 8) & 4278255360;
			n[(c + 64 >>> 9 << 4) + 14] = (a << 8 | a >>> 24) & 16711935 | (a << 24 | a >>> 8) & 4278255360;
			b.sigBytes = 4 * (n.length + 1);
			this.ng();
			b = this.eo;
			n = b.words;
			for (a = 0; 4 > a; a++) c = n[a], n[a] = (c << 8 | c >>> 24) & 16711935 | (c << 24 | c >>> 8) & 4278255360;
			return b
		},
		clone: function() {
			var b = v.clone.call(this);
			b.eo = this.eo.clone();
			return b
		}
	});
	t.MD5 = v.Az(r);
	t.HmacMD5 = v.Mr(r)
})(Math);
(function() {
	var u = CryptoJS,
		p = u.lib,
		d = p.Base,
		l = p.WordArray,
		p = u.algo,
		s = p.EvpKDF = d.extend({
			cfg: d.extend({
				keySize: 4,
				hasher: p.MD5,
				iterations: 1
			}),
			init: function(d) {
				this.cfg = this.cfg.extend(d)
			},
			compute: function(d, r) {
				for (var p = this.cfg, s = p.hasher.create(), b = l.create(), u = b.words, q = p.keySize, p = p.iterations; u.length < q;) {
					n && s.update(n);
					var n = s.update(d).finalize(r);
					s.reset();
					for (var a = 1; a < p; a++) n = s.finalize(n), s.reset();
					b.concat(n)
				}
				b.sigBytes = 4 * q;
				return b
			}
		});
	u.EvpKDF = function(d, l, p) {
		return s.create(p).compute(d, l)
	}
})();
CryptoJS.lib.Cipher || function(u) {
	var p = CryptoJS,
		d = p.lib,
		l = d.Base,
		s = d.WordArray,
		t = d.BufferedBlockAlgorithm,
		r = p.enc.Base64,
		w = p.algo.EvpKDF,
		v = d.Cipher = t.extend({
			cfg: l.extend(),
			createEncryptor: function(e, a) {
				return this.create(this.AD, e, a)
			},
			createDecryptor: function(e, a) {
				return this.create(this.Mm, e, a)
			},
			init: function(e, a, b) {
				this.cfg = this.cfg.extend(b);
				this.CB = e;
				this.bba = a;
				this.reset()
			},
			reset: function() {
				t.reset.call(this);
				this.AB()
			},
			process: function(e) {
				this.uu(e);
				return this.ng()
			},
			finalize: function(e) {
				e && this.uu(e);
				return this.uA()
			},
			keySize: 4,
			ivSize: 4,
			AD: 1,
			Mm: 2,
			Az: function(e) {
				return {
					encrypt: function(b, k, d) {
						return ("string" == typeof k ? c : a).encrypt(e, b, k, d)
					},
					decrypt: function(b, k, d) {
						return ("string" == typeof k ? c : a).decrypt(e, b, k, d)
					}
				}
			}
		});
	d.StreamCipher = v.extend({
		uA: function() {
			return this.ng(!0)
		},
		blockSize: 1
	});
	var b = p.mode = {},
		x = function(e, a, b) {
			var c = this.Cz;
			c ? this.Cz = u : c = this.Cy;
			for (var d = 0; d < b; d++) e[a + d] ^= c[d]
		},
		q = (d.BlockCipherMode = l.extend({
			createEncryptor: function(e, a) {
				return this.Encryptor.create(e, a)
			},
			createDecryptor: function(e, a) {
				return this.Decryptor.create(e, a)
			},
			init: function(e, a) {
				this.Cx = e;
				this.Cz = a
			}
		})).extend();
	q.Encryptor = q.extend({
		processBlock: function(e, a) {
			var b = this.Cx,
				c = b.blockSize;
			x.call(this, e, a, c);
			b.encryptBlock(e, a);
			this.Cy = e.slice(a, a + c)
		}
	});
	q.Decryptor = q.extend({
		processBlock: function(e, a) {
			var b = this.Cx,
				c = b.blockSize,
				d = e.slice(a, a + c);
			b.decryptBlock(e, a);
			x.call(this, e, a, c);
			this.Cy = d
		}
	});
	b = b.CBC = q;
	q = (p.pad = {}).Pkcs7 = {
		pad: function(a, b) {
			for (var c = 4 * b, c = c - a.sigBytes % c, d = c << 24 | c << 16 | c << 8 | c, l = [], n = 0; n < c; n += 4) l.push(d);
			c = s.create(l, c);
			a.concat(c)
		},
		unpad: function(a) {
			a.sigBytes -= a.words[a.sigBytes - 1 >>> 2] & 255
		}
	};
	d.BlockCipher = v.extend({
		cfg: v.cfg.extend({
			mode: b,
			padding: q
		}),
		reset: function() {
			v.reset.call(this);
			var a = this.cfg,
				b = a.iv,
				a = a.mode;
			if (this.CB == this.AD) var c = a.createEncryptor;
			else c = a.createDecryptor, this.CE = 1;
			this.dS = c.call(a, this, b && b.words)
		},
		CC: function(a, b) {
			this.dS.processBlock(a, b)
		},
		uA: function() {
			var a = this.cfg.padding;
			if (this.CB == this.AD) {
				a.pad(this.bk, this.blockSize);
				var b = this.ng(!0)
			} else b = this.ng(!0), a.unpad(b);
			return b
		},
		blockSize: 4
	});
	var n = d.CipherParams = l.extend({
			init: function(a) {
				this.mixIn(a)
			},
			toString: function(a) {
				return (a || this.formatter).stringify(this)
			}
		}),
		b = (p.format = {}).OpenSSL = {
			stringify: function(a) {
				var b = a.ciphertext;
				a = a.salt;
				return (a ? s.create([1398893684, 1701076831]).concat(a).concat(b) : b).toString(r)
			},
			parse: function(a) {
				a = r.parse(a);
				var b = a.words;
				if (1398893684 == b[0] && 1701076831 == b[1]) {
					var c = s.create(b.slice(2, 4));
					b.splice(0, 4);
					a.sigBytes -= 16
				}
				return n.create({
					ciphertext: a,
					salt: c
				})
			}
		},
		a = d.SerializableCipher = l.extend({
			cfg: l.extend({
				format: b
			}),
			encrypt: function(a, b, c, d) {
				d = this.cfg.extend(d);
				var l = a.createEncryptor(c, d);
				b = l.finalize(b);
				l = l.cfg;
				return n.create({
					ciphertext: b,
					key: c,
					iv: l.iv,
					algorithm: a,
					mode: l.mode,
					padding: l.padding,
					blockSize: a.blockSize,
					formatter: d.format
				})
			},
			decrypt: function(a, b, c, d) {
				d = this.cfg.extend(d);
				b = this.uk(b, d.format);
				return a.createDecryptor(c, d).finalize(b.ciphertext)
			},
			uk: function(a, b) {
				return "string" == typeof a ? b.parse(a, this) : a
			}
		}),
		p = (p.kdf = {}).OpenSSL = {
			execute: function(a, b, c, d) {
				d || (d = s.random(8));
				a = w.create({
					keySize: b + c
				}).compute(a, d);
				c = s.create(a.words.slice(b), 4 * c);
				a.sigBytes = 4 * b;
				return n.create({
					key: a,
					iv: c,
					salt: d
				})
			}
		},
		c = d.PasswordBasedCipher = a.extend({
			cfg: a.cfg.extend({
				kdf: p
			}),
			encrypt: function(b, c, d, l) {
				l = this.cfg.extend(l);
				d = l.kdf.execute(d, b.keySize, b.ivSize);
				l.iv = d.iv;
				b = a.encrypt.call(this, b, c, d.key, l);
				b.mixIn(d);
				return b
			},
			decrypt: function(b, c, d, l) {
				l = this.cfg.extend(l);
				c = this.uk(c, l.format);
				d = l.kdf.execute(d, b.keySize, b.ivSize, c.salt);
				l.iv = d.iv;
				return a.decrypt.call(this, b, c, d.key, l)
			}
		})
}();
(function() {
	for (var u = CryptoJS, p = u.lib.BlockCipher, d = u.algo, l = [], s = [], t = [], r = [], w = [], v = [], b = [], x = [], q = [], n = [], a = [], c = 0; 256 > c; c++) a[c] = 128 > c ? c << 1 : c << 1 ^ 283;
	for (var e = 0, j = 0, c = 0; 256 > c; c++) {
		var k = j ^ j << 1 ^ j << 2 ^ j << 3 ^ j << 4,
			k = k >>> 8 ^ k & 255 ^ 99;
		l[e] = k;
		s[k] = e;
		var z = a[e],
			F = a[z],
			G = a[F],
			y = 257 * a[k] ^ 16843008 * k;
		t[e] = y << 24 | y >>> 8;
		r[e] = y << 16 | y >>> 16;
		w[e] = y << 8 | y >>> 24;
		v[e] = y;
		y = 16843009 * G ^ 65537 * F ^ 257 * z ^ 16843008 * e;
		b[k] = y << 24 | y >>> 8;
		x[k] = y << 16 | y >>> 16;
		q[k] = y << 8 | y >>> 24;
		n[k] = y;
		e ? (e = z ^ a[a[a[G ^ z]]], j ^= a[a[j]]) : e = j = 1
	}
	var H = [0, 1, 2, 4, 8, 16, 32, 64, 128, 27, 54],
		d = d.AES = p.extend({
			AB: function() {
				for (var a = this.bba, c = a.words, d = a.sigBytes / 4, a = 4 * ((this.UR = d + 6) + 1), e = this.UM = [], j = 0; j < a; j++)
					if (j < d) e[j] = c[j];
					else {
						var k = e[j - 1];
						j % d ? 6 < d && 4 == j % d && (k = l[k >>> 24] << 24 | l[k >>> 16 & 255] << 16 | l[k >>> 8 & 255] << 8 | l[k & 255]) : (k = k << 8 | k >>> 24, k = l[k >>> 24] << 24 | l[k >>> 16 & 255] << 16 | l[k >>> 8 & 255] << 8 | l[k & 255], k ^= H[j / d | 0] << 24);
						e[j] = e[j - d] ^ k
					}
				c = this.Um = [];
				for (d = 0; d < a; d++) j = a - d, k = d % 4 ? e[j] : e[j - 4], c[d] = 4 > d || 4 >= j ? k : b[l[k >>> 24]] ^ x[l[k >>> 16 & 255]] ^ q[l[k >>> 8 & 255]] ^ n[l[k & 255]]
			},
			encryptBlock: function(a, b) {
				this.Cw(a, b, this.UM, t, r, w, v, l)
			},
			decryptBlock: function(a, c) {
				var d = a[c + 1];
				a[c + 1] = a[c + 3];
				a[c + 3] = d;
				this.Cw(a, c, this.Um, b, x, q, n, s);
				d = a[c + 1];
				a[c + 1] = a[c + 3];
				a[c + 3] = d
			},
			Cw: function(a, b, c, d, e, j, l, f) {
				for (var m = this.UR, g = a[b] ^ c[0], h = a[b + 1] ^ c[1], k = a[b + 2] ^ c[2], n = a[b + 3] ^ c[3], p = 4, r = 1; r < m; r++) var q = d[g >>> 24] ^ e[h >>> 16 & 255] ^ j[k >>> 8 & 255] ^ l[n & 255] ^ c[p++],
					s = d[h >>> 24] ^ e[k >>> 16 & 255] ^ j[n >>> 8 & 255] ^ l[g & 255] ^ c[p++],
					t = d[k >>> 24] ^ e[n >>> 16 & 255] ^ j[g >>> 8 & 255] ^ l[h & 255] ^ c[p++],
					n = d[n >>> 24] ^ e[g >>> 16 & 255] ^ j[h >>> 8 & 255] ^ l[k & 255] ^ c[p++],
					g = q,
					h = s,
					k = t;
				q = (f[g >>> 24] << 24 | f[h >>> 16 & 255] << 16 | f[k >>> 8 & 255] << 8 | f[n & 255]) ^ c[p++];
				s = (f[h >>> 24] << 24 | f[k >>> 16 & 255] << 16 | f[n >>> 8 & 255] << 8 | f[g & 255]) ^ c[p++];
				t = (f[k >>> 24] << 24 | f[n >>> 16 & 255] << 16 | f[g >>> 8 & 255] << 8 | f[h & 255]) ^ c[p++];
				n = (f[n >>> 24] << 24 | f[g >>> 16 & 255] << 16 | f[h >>> 8 & 255] << 8 | f[k & 255]) ^ c[p++];
				a[b] = q;
				a[b + 1] = s;
				a[b + 2] = t;
				a[b + 3] = n
			},
			keySize: 8
		});
	u.AES = p.Az(d)
})();

function RSAKeyPair(a, b, c) {
	this.e = biFromHex(a), this.d = biFromHex(b), this.m = biFromHex(c), this.chunkSize = 2 * biHighIndex(this.m), this.radix = 16, this.barrett = new BarrettMu(this.m)
}

function twoDigit(a) {
	return (10 > a ? "0" : "") + String(a)
}

function encryptedString(a, b) {
	for (var f, g, h, i, j, k, l, c = new Array, d = b.length, e = 0; d > e;) c[e] = b.charCodeAt(e), e++;
	for (; 0 != c.length % a.chunkSize;) c[e++] = 0;
	for (f = c.length, g = "", e = 0; f > e; e += a.chunkSize) {
		for (j = new BigInt, h = 0, i = e; i < e + a.chunkSize; ++h) j.digits[h] = c[i++], j.digits[h] += c[i++] << 8;
		k = a.barrett.powMod(j, a.e), l = 16 == a.radix ? biToHex(k) : biToString(k, a.radix), g += l + " "
	}
	return g.substring(0, g.length - 1)
}

function decryptedString(a, b) {
	var e, f, g, h, c = b.split(" "),
		d = "";
	for (e = 0; e < c.length; ++e)
		for (h = 16 == a.radix ? biFromHex(c[e]) : biFromString(c[e], a.radix), g = a.barrett.powMod(h, a.d), f = 0; f <= biHighIndex(g); ++f) d += String.fromCharCode(255 & g.digits[f], g.digits[f] >> 8);
	return 0 == d.charCodeAt(d.length - 1) && (d = d.substring(0, d.length - 1)), d
}

function setMaxDigits(a) {
	maxDigits = a, ZERO_ARRAY = new Array(maxDigits);
	for (var b = 0; b < ZERO_ARRAY.length; b++) ZERO_ARRAY[b] = 0;
	bigZero = new BigInt, bigOne = new BigInt, bigOne.digits[0] = 1
}

function BigInt(a) {
	this.digits = "boolean" == typeof a && 1 == a ? null : ZERO_ARRAY.slice(0), this.isNeg = !1
}

function biFromDecimal(a) {
	for (var d, e, f, b = "-" == a.charAt(0), c = b ? 1 : 0; c < a.length && "0" == a.charAt(c);) ++c;
	if (c == a.length) d = new BigInt;
	else {
		for (e = a.length - c, f = e % dpl10, 0 == f && (f = dpl10), d = biFromNumber(Number(a.substr(c, f))), c += f; c < a.length;) d = biAdd(biMultiply(d, lr10), biFromNumber(Number(a.substr(c, dpl10)))), c += dpl10;
		d.isNeg = b
	}
	return d
}

function biCopy(a) {
	var b = new BigInt(!0);
	return b.digits = a.digits.slice(0), b.isNeg = a.isNeg, b
}

function biFromNumber(a) {
	var c, b = new BigInt;
	for (b.isNeg = 0 > a, a = Math.abs(a), c = 0; a > 0;) b.digits[c++] = a & maxDigitVal, a >>= biRadixBits;
	return b
}

function reverseStr(a) {
	var c, b = "";
	for (c = a.length - 1; c > -1; --c) b += a.charAt(c);
	return b
}

function biToString(a, b) {
	var d, e, c = new BigInt;
	for (c.digits[0] = b, d = biDivideModulo(a, c), e = hexatrigesimalToChar[d[1].digits[0]]; 1 == biCompare(d[0], bigZero);) d = biDivideModulo(d[0], c), digit = d[1].digits[0], e += hexatrigesimalToChar[d[1].digits[0]];
	return (a.isNeg ? "-" : "") + reverseStr(e)
}

function biToDecimal(a) {
	var c, d, b = new BigInt;
	for (b.digits[0] = 10, c = biDivideModulo(a, b), d = String(c[1].digits[0]); 1 == biCompare(c[0], bigZero);) c = biDivideModulo(c[0], b), d += String(c[1].digits[0]);
	return (a.isNeg ? "-" : "") + reverseStr(d)
}

function digitToHex(a) {
	var b = 15,
		c = "";
	for (i = 0; 4 > i; ++i) c += hexToChar[a & b], a >>>= 4;
	return reverseStr(c)
}

function biToHex(a) {
	var d, b = "";
	for (biHighIndex(a), d = biHighIndex(a); d > -1; --d) b += digitToHex(a.digits[d]);
	return b
}

function charToHex(a) {
	var h, b = 48,
		c = b + 9,
		d = 97,
		e = d + 25,
		f = 65,
		g = 90;
	return h = a >= b && c >= a ? a - b : a >= f && g >= a ? 10 + a - f : a >= d && e >= a ? 10 + a - d : 0
}

function hexToDigit(a) {
	var d, b = 0,
		c = Math.min(a.length, 4);
	for (d = 0; c > d; ++d) b <<= 4, b |= charToHex(a.charCodeAt(d));
	return b
}

function biFromHex(a) {
	var d, e, b = new BigInt,
		c = a.length;
	for (d = c, e = 0; d > 0; d -= 4, ++e) b.digits[e] = hexToDigit(a.substr(Math.max(d - 4, 0), Math.min(d, 4)));
	return b
}

function biFromString(a, b) {
	var g, h, i, j, c = "-" == a.charAt(0),
		d = c ? 1 : 0,
		e = new BigInt,
		f = new BigInt;
	for (f.digits[0] = 1, g = a.length - 1; g >= d; g--) h = a.charCodeAt(g), i = charToHex(h), j = biMultiplyDigit(f, i), e = biAdd(e, j), f = biMultiplyDigit(f, b);
	return e.isNeg = c, e
}

function biDump(a) {
	return (a.isNeg ? "-" : "") + a.digits.join(" ")
}

function biAdd(a, b) {
	var c, d, e, f;
	if (a.isNeg != b.isNeg) b.isNeg = !b.isNeg, c = biSubtract(a, b), b.isNeg = !b.isNeg;
	else {
		for (c = new BigInt, d = 0, f = 0; f < a.digits.length; ++f) e = a.digits[f] + b.digits[f] + d, c.digits[f] = 65535 & e, d = Number(e >= biRadix);
		c.isNeg = a.isNeg
	}
	return c
}

function biSubtract(a, b) {
	var c, d, e, f;
	if (a.isNeg != b.isNeg) b.isNeg = !b.isNeg, c = biAdd(a, b), b.isNeg = !b.isNeg;
	else {
		for (c = new BigInt, e = 0, f = 0; f < a.digits.length; ++f) d = a.digits[f] - b.digits[f] + e, c.digits[f] = 65535 & d, c.digits[f] < 0 && (c.digits[f] += biRadix), e = 0 - Number(0 > d);
		if (-1 == e) {
			for (e = 0, f = 0; f < a.digits.length; ++f) d = 0 - c.digits[f] + e, c.digits[f] = 65535 & d, c.digits[f] < 0 && (c.digits[f] += biRadix), e = 0 - Number(0 > d);
			c.isNeg = !a.isNeg
		} else c.isNeg = a.isNeg
	}
	return c
}

function biHighIndex(a) {
	for (var b = a.digits.length - 1; b > 0 && 0 == a.digits[b];) --b;
	return b
}

function biNumBits(a) {
	var e, b = biHighIndex(a),
		c = a.digits[b],
		d = (b + 1) * bitsPerDigit;
	for (e = d; e > d - bitsPerDigit && 0 == (32768 & c); --e) c <<= 1;
	return e
}

function biMultiply(a, b) {
	var d, h, i, k, c = new BigInt,
		e = biHighIndex(a),
		f = biHighIndex(b);
	for (k = 0; f >= k; ++k) {
		for (d = 0, i = k, j = 0; e >= j; ++j, ++i) h = c.digits[i] + a.digits[j] * b.digits[k] + d, c.digits[i] = h & maxDigitVal, d = h >>> biRadixBits;
		c.digits[k + e + 1] = d
	}
	return c.isNeg = a.isNeg != b.isNeg, c
}

function biMultiplyDigit(a, b) {
	var c, d, e, f;
	for (result = new BigInt, c = biHighIndex(a), d = 0, f = 0; c >= f; ++f) e = result.digits[f] + a.digits[f] * b + d, result.digits[f] = e & maxDigitVal, d = e >>> biRadixBits;
	return result.digits[1 + c] = d, result
}

function arrayCopy(a, b, c, d, e) {
	var g, h, f = Math.min(b + e, a.length);
	for (g = b, h = d; f > g; ++g, ++h) c[h] = a[g]
}

function biShiftLeft(a, b) {
	var e, f, g, h, c = Math.floor(b / bitsPerDigit),
		d = new BigInt;
	for (arrayCopy(a.digits, 0, d.digits, c, d.digits.length - c), e = b % bitsPerDigit, f = bitsPerDigit - e, g = d.digits.length - 1, h = g - 1; g > 0; --g, --h) d.digits[g] = d.digits[g] << e & maxDigitVal | (d.digits[h] & highBitMasks[e]) >>> f;
	return d.digits[0] = d.digits[g] << e & maxDigitVal, d.isNeg = a.isNeg, d
}

function biShiftRight(a, b) {
	var e, f, g, h, c = Math.floor(b / bitsPerDigit),
		d = new BigInt;
	for (arrayCopy(a.digits, c, d.digits, 0, a.digits.length - c), e = b % bitsPerDigit, f = bitsPerDigit - e, g = 0, h = g + 1; g < d.digits.length - 1; ++g, ++h) d.digits[g] = d.digits[g] >>> e | (d.digits[h] & lowBitMasks[e]) << f;
	return d.digits[d.digits.length - 1] >>>= e, d.isNeg = a.isNeg, d
}

function biMultiplyByRadixPower(a, b) {
	var c = new BigInt;
	return arrayCopy(a.digits, 0, c.digits, b, c.digits.length - b), c
}

function biDivideByRadixPower(a, b) {
	var c = new BigInt;
	return arrayCopy(a.digits, b, c.digits, 0, c.digits.length - b), c
}

function biModuloByRadixPower(a, b) {
	var c = new BigInt;
	return arrayCopy(a.digits, 0, c.digits, 0, b), c
}

function biCompare(a, b) {
	if (a.isNeg != b.isNeg) return 1 - 2 * Number(a.isNeg);
	for (var c = a.digits.length - 1; c >= 0; --c)
		if (a.digits[c] != b.digits[c]) return a.isNeg ? 1 - 2 * Number(a.digits[c] > b.digits[c]) : 1 - 2 * Number(a.digits[c] < b.digits[c]);
	return 0
}

function biDivideModulo(a, b) {
	var f, g, h, i, j, k, l, m, n, o, p, q, r, s, c = biNumBits(a),
		d = biNumBits(b),
		e = b.isNeg;
	if (d > c) return a.isNeg ? (f = biCopy(bigOne), f.isNeg = !b.isNeg, a.isNeg = !1, b.isNeg = !1, g = biSubtract(b, a), a.isNeg = !0, b.isNeg = e) : (f = new BigInt, g = biCopy(a)), new Array(f, g);
	for (f = new BigInt, g = a, h = Math.ceil(d / bitsPerDigit) - 1, i = 0; b.digits[h] < biHalfRadix;) b = biShiftLeft(b, 1), ++i, ++d, h = Math.ceil(d / bitsPerDigit) - 1;
	for (g = biShiftLeft(g, i), c += i, j = Math.ceil(c / bitsPerDigit) - 1, k = biMultiplyByRadixPower(b, j - h); - 1 != biCompare(g, k);) ++f.digits[j - h], g = biSubtract(g, k);
	for (l = j; l > h; --l) {
		for (m = l >= g.digits.length ? 0 : g.digits[l], n = l - 1 >= g.digits.length ? 0 : g.digits[l - 1], o = l - 2 >= g.digits.length ? 0 : g.digits[l - 2], p = h >= b.digits.length ? 0 : b.digits[h], q = h - 1 >= b.digits.length ? 0 : b.digits[h - 1], f.digits[l - h - 1] = m == p ? maxDigitVal : Math.floor((m * biRadix + n) / p), r = f.digits[l - h - 1] * (p * biRadix + q), s = m * biRadixSquared + (n * biRadix + o); r > s;) --f.digits[l - h - 1], r = f.digits[l - h - 1] * (p * biRadix | q), s = m * biRadix * biRadix + (n * biRadix + o);
		k = biMultiplyByRadixPower(b, l - h - 1), g = biSubtract(g, biMultiplyDigit(k, f.digits[l - h - 1])), g.isNeg && (g = biAdd(g, k), --f.digits[l - h - 1])
	}
	return g = biShiftRight(g, i), f.isNeg = a.isNeg != e, a.isNeg && (f = e ? biAdd(f, bigOne) : biSubtract(f, bigOne), b = biShiftRight(b, i), g = biSubtract(b, g)), 0 == g.digits[0] && 0 == biHighIndex(g) && (g.isNeg = !1), new Array(f, g)
}

function biDivide(a, b) {
	return biDivideModulo(a, b)[0]
}

function biModulo(a, b) {
	return biDivideModulo(a, b)[1]
}

function biMultiplyMod(a, b, c) {
	return biModulo(biMultiply(a, b), c)
}

function biPow(a, b) {
	for (var c = bigOne, d = a;;) {
		if (0 != (1 & b) && (c = biMultiply(c, d)), b >>= 1, 0 == b) break;
		d = biMultiply(d, d)
	}
	return c
}

function biPowMod(a, b, c) {
	for (var d = bigOne, e = a, f = b;;) {
		if (0 != (1 & f.digits[0]) && (d = biMultiplyMod(d, e, c)), f = biShiftRight(f, 1), 0 == f.digits[0] && 0 == biHighIndex(f)) break;
		e = biMultiplyMod(e, e, c)
	}
	return d
}

function BarrettMu(a) {
	this.modulus = biCopy(a), this.k = biHighIndex(this.modulus) + 1;
	var b = new BigInt;
	b.digits[2 * this.k] = 1, this.mu = biDivide(b, this.modulus), this.bkplus1 = new BigInt, this.bkplus1.digits[this.k + 1] = 1, this.modulo = BarrettMu_modulo, this.multiplyMod = BarrettMu_multiplyMod, this.powMod = BarrettMu_powMod
}

function BarrettMu_modulo(a) {
	var i, b = biDivideByRadixPower(a, this.k - 1),
		c = biMultiply(b, this.mu),
		d = biDivideByRadixPower(c, this.k + 1),
		e = biModuloByRadixPower(a, this.k + 1),
		f = biMultiply(d, this.modulus),
		g = biModuloByRadixPower(f, this.k + 1),
		h = biSubtract(e, g);
	for (h.isNeg && (h = biAdd(h, this.bkplus1)), i = biCompare(h, this.modulus) >= 0; i;) h = biSubtract(h, this.modulus), i = biCompare(h, this.modulus) >= 0;
	return h
}

function BarrettMu_multiplyMod(a, b) {
	var c = biMultiply(a, b);
	return this.modulo(c)
}

function BarrettMu_powMod(a, b) {
	var d, e, c = new BigInt;
	for (c.digits[0] = 1, d = a, e = b;;) {
		if (0 != (1 & e.digits[0]) && (c = this.multiplyMod(c, d)), e = biShiftRight(e, 1), 0 == e.digits[0] && 0 == biHighIndex(e)) break;
		d = this.multiplyMod(d, d)
	}
	return c
}
var maxDigits, ZERO_ARRAY, bigZero, bigOne, dpl10, lr10, hexatrigesimalToChar, hexToChar, highBitMasks, lowBitMasks, biRadixBase = 2,
	biRadixBits = 16,
	bitsPerDigit = biRadixBits,
	biRadix = 65536,
	biHalfRadix = biRadix >>> 1,
	biRadixSquared = biRadix * biRadix,
	maxDigitVal = biRadix - 1,
	maxInteger = 9999999999999998;
setMaxDigits(20), dpl10 = 15, lr10 = biFromNumber(1e15), hexatrigesimalToChar = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"), hexToChar = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"), highBitMasks = new Array(0, 32768, 49152, 57344, 61440, 63488, 64512, 65024, 65280, 65408, 65472, 65504, 65520, 65528, 65532, 65534, 65535), lowBitMasks = new Array(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535);
! function() {
	function a(a) {
		var d, e, b = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
			c = "";
		for (d = 0; a > d; d += 1) e = Math.random() * b.length, e = Math.floor(e), c += b.charAt(e);
		return c
	}

	function b(a, b) {
		var c = CryptoJS.enc.Utf8.parse(b),
			d = CryptoJS.enc.Utf8.parse("0102030405060708"),
			e = CryptoJS.enc.Utf8.parse(a),
			f = CryptoJS.AES.encrypt(e, c, {
				iv: d,
				mode: CryptoJS.mode.CBC
			});
		return f.toString()
	}

	function c(a, b, c) {
		var d, e;
		return setMaxDigits(131), d = new RSAKeyPair(b, "", c), e = encryptedString(d, a)
	}

	function d(d, e, f, g) {
		var h = {},
			i = a(16);
		return h.encText = b(d, g), h.encText = b(h.encText, i), h.encSecKey = c(i, e, f), h
	}

	function e(a, b, d, e) {
		var f = {};
		return f.encText = c(a + e, b, d), f
	}
	window.asrsea = d, window.ecnonasr = e
}();
(function() {
	var bh = NEJ.P,
		dJ = bh("nej.g"),
		bE = bh("nej.j"),
		bm = bh("nej.u"),
		rf = bh("nm.x.ek"),
		bz = bh("nm.x");
	if (bE.cN.redefine || !window.GEnc) return;
	var AN = function(TC) {
		var bw = [];
		bm.bfq(TC, function(SQ) {
			bw.push(rf.emj[SQ])
		});
		return bw.join("")
	};
	var SG = bE.cN;
	bE.cN = function(bC, bf) {
		var bk = {},
			bf = NEJ.X({}, bf),
			mj = bC.indexOf("?");
		if (/(^|\.com)\/api/.test(bC) && !(bf.headers && bf.headers[dJ.lS] == dJ.nC) && !bf.noEnc) {
			if (mj != -1) {
				bk = bm.fQ(bC.substring(mj + 1));
				bC = bC.substring(0, mj)
			}
			if (bf.query) {
				bk = NEJ.X(bk, bm.dW(bf.query) ? bm.fQ(bf.query) : bf.query)
			}
			if (bf.data) {
				bk = NEJ.X(bk, bm.dW(bf.data) ? bm.fQ(bf.data) : bf.data)
			}
			bk["csrf_token"] = bE.mr("__csrf");
			bC = bC.replace("api", "weapi");
			bf.method = "post";
			delete bf.query;
			var Cu = window.asrsea(JSON.stringify(bk), AN(["流泪", "强"]), AN(rf.md), AN(["爱心", "女孩", "惊恐", "大笑"]));
			bf.data = bm.fr({
				params: Cu.encText,
				encSecKey: Cu.encSecKey
			})
		}
		SG(bC, bf)
	};
	bE.cN.redefine = true
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		dI = bh("nej.p"),
		bm = bh("nej.u"),
		bo = bh("nej.v"),
		it = bh("nm.u"),
		bz = bh("nm.x"),
		bD = bh("nm.d"),
		RZ = "http://s1.music.126.net/style/web2/emt/emoji_{ID}.png",
		bk = {
			"大笑": "86",
			"可爱": "85",
			"憨笑": "359",
			"色": "95",
			"亲亲": "363",
			"惊恐": "96",
			"流泪": "356",
			"亲": "362",
			"呆": "352",
			"哀伤": "342",
			"呲牙": "343",
			"吐舌": "348",
			"撇嘴": "353",
			"怒": "361",
			"奸笑": "341",
			"汗": "97",
			"痛苦": "346",
			"惶恐": "354",
			"生病": "350",
			"口罩": "351",
			"大哭": "357",
			"晕": "355",
			"发怒": "115",
			"开心": "360",
			"鬼脸": "94",
			"皱眉": "87",
			"流感": "358",
			"爱心": "33",
			"心碎": "34",
			"钟情": "303",
			"星星": "309",
			"生气": "314",
			"便便": "89",
			"强": "13",
			"弱": "372",
			"拜": "14",
			"牵手": "379",
			"跳舞": "380",
			"禁止": "374",
			"这边": "262",
			"爱意": "106",
			"示爱": "376",
			"嘴唇": "367",
			"狗": "81",
			"猫": "78",
			"猪": "100",
			"兔子": "459",
			"小鸡": "450",
			"公鸡": "461",
			"幽灵": "116",
			"圣诞": "411",
			"外星": "101",
			"钻石": "52",
			"礼物": "107",
			"男孩": "0",
			"女孩": "1",
			"蛋糕": "337",
			18: "186",
			"圈": "312",
			"叉": "313"
		};
	bz.bap = function(dD) {
		if (!dD || dD.copyrightId === undefined || !!dD.program) return false;
		if (window.GAbroad) {
			dD.fee = 0;
			return true
		}
		if (dD.status < 0) return true;
		var rm;
		if (typeof GCopyrights !== "undefined") rm = GCopyrights;
		try {
			if (!rm && !!top.GCopyrights) rm = top.GCopyrights
		} catch (e) {}
		if (rm) {
			var bfo = bm.fM(rm, dD.copyrightId);
			if (bfo >= 0) return true
		}
		return false
	};
	bz.RK = function() {
		var AX = /^\/m\/(song|album|artist|playlist|dj|search)\?/,
			kn = {
				2: "artist",
				13: "playlist",
				17: "dj",
				19: "album",
				18: "song",
				31: "toplist",
				32: "searchsong",
				33: "searchlyric",
				34: "event",
				70: "djradio",
				24: "day",
				50: "record"
			},
			QC = {
				song: "单曲",
				album: "专辑",
				artist: "歌手",
				playlist: "歌单",
				dj: "电台节目",
				searchsong: "单曲搜索",
				searchlyric: "歌词搜索",
				toplist: "榜单",
				event: "动态",
				djradio: "电台",
				day: "每日歌曲推荐",
				record: "听歌排行榜"
			};
		var Qp = function(bba, bk, ro) {
			switch (bba) {
				case "event":
					bk = bk.split("|");
					return "/event?id=" + bk[0] + "&uid=" + bk[1];
				case "searchsong":
				case "searchlyric":
					var bv = bba == "searchsong" ? 1 : 1006;
					return "/search/m/?s=" + encodeURIComponent(bk) + "&type=" + bv;
				case "toplist":
					return "/discover/toplist?id=" + bk + "&_hash=songlist-" + ro;
				case "day":
					return "/discover/recommend/taste" + "?_hash=songlist-" + ro;;
				case "record":
					bk = bk.split("|");
					return "/user/songs/rank?id=" + bk[0] + "&cat=" + bk[1];
					break;
				default:
					return "/" + bba + "?id=" + bk + "&_hash=songlist-" + ro
			}
		};
		return function(cD, ro) {
			if (!cD) return null;
			var qO = cD.fid || (cD.type != 18 ? cD.type : null),
				Cr = cD.fdata || cD.rid,
				Cq = cD.page || cD.fhref;
			var bba = kn[qO];
			if (!bba) {
				var Cm = (Cq || "").match(AX);
				if (Cm) bba = Cm[1]
			}
			if (!bba) return null;
			return {
				title: QC[bba],
				link: !kn[qO] ? Cq : Qp(bba, Cr, ro),
				fid: qO,
				fdata: Cr
			}
		}
	}();
	bz.baA = function(ku) {
		var mF = ku;
		if (typeof GUser !== "undefined" && GUser.userId > 0) mF = GUser;
		return mF
	};
	bz.CL = function() {
		if (typeof GUser !== "undefined" && GUser.userId > 0) {
			return true
		} else {
			top.login();
			return false
		}
	};
	bz.Cl = function() {
		var AX = /#(.*?)$/;
		return function(jP) {
			var eC = jP === false ? location : top.location;
			return AX.test(eC.href) ? RegExp.$1 : ""
		}
	}();
	bz.baB = function() {
		var Ch = bz.BY().supported,
			fo = bg.hw("audio"),
			Cf = fo.canPlayType && fo.canPlayType("audio/mpeg");
		if (dI.tm.mac) {
			if (Cf) return 2;
			if (Ch) return 1;
			return 0
		} else {
			if (Ch) return 1;
			if (Cf) return 2;
			return 0
		}
	};
	bz.BY = function() {
		var ds, Br = !1,
			Bs = "";
		if (dI.eV.browser == "ie") {
			try {
				ds = new ActiveXObject("ShockwaveFlash.ShockwaveFlash")
			} catch (e) {
				ds = null
			}
			if (ds) {
				Br = !0;
				Bs = ds.GetVariable("$version")
			}
		} else {
			if (navigator.plugins && navigator.plugins.length > 0) {
				ds = navigator.plugins["Shockwave Flash"];
				if (ds) {
					Br = !0;
					Bs = ds.description
				}
			}
		}
		return {
			supported: Br,
			version: Bs
		}
	};
	bz.baO = function() {
		return "网易云音乐 听见好时光"
	};
	bz.MD = function() {
		return bk
	};
	bz.BU = function(dR) {
		var bt = bk[dR];
		return bt == null ? "" : RZ.replace("{ID}", bt)
	};
	bz.WJ = function(bU, cZ, Bx) {
		if (!bU) return "";
		if (!!Bx) {
			bU = bz.VE(bU)
		}
		return bz.vg(bz.Sl(bU, cZ))
	};
	bz.VE = function() {
		var cA = new RegExp("(http[s]{0,1})://[-a-zA-Z0-9.]+(:(6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]|\\d{2,4}|[1-9]))?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?", "g");
		return function(bU) {
			return (bU || "").replace(/&amp;/g, "&").replace(/&nbsp;/g, " ").replace(cA, function($0, $1) {
				return "<a href=" + $0 + ' class="link u-link"><i class="u-dicn u-dicn-28"></i>网页链接</a>'
			})
		}
	}();
	bz.Sl = function() {
		var cA = /@([a-zA-Z0-9_\-\u4E00-\u9FA5]+)/g;
		var lO = function(BR, cZ) {
			return '<a href="/user/home?nickname=' + encodeURIComponent(BR) + '" class="' + (cZ || "") + '">@' + BR + "</a>"
		};
		return function(bU, cZ) {
			return (bU || "").replace(cA, function($0, $1) {
				return lO($1, cZ)
			})
		}
	}();
	bz.vg = function() {
		var cA = /\[(.*?)\]/g;
		return function(bU) {
			return (bU || "").replace(cA, function($1, $2) {
				var bC = bz.BU($2);
				return !bC ? $1 : '<img src="' + bC + '"/>'
			})
		}
	}();
	bz.xt = function(bp, cZ) {
		bg.eF(bp, cZ) ? bg.cr(bp, cZ) : bg.cu(bp, cZ)
	};
	bz.Mw = function(cW, fD) {
		cW = bg.bL(cW);
		fD = bg.bL(fD);
		if (!cW || !fD) return !1;
		for (fD = fD.parentNode; !!fD && fD != cW; fD = fD.parentNode);
		return fD == cW
	};
	bz.baQ = function() {
		var rz = function(dP) {
			return (dP < 10 ? "0" : "") + dP
		};
		return function(iH) {
			iH = parseInt(iH) || 0;
			if (!iH) return "00:00";
			var BG = Math.floor(iH % 60),
				BH = Math.floor(iH / 60);
			return rz(BH) + ":" + rz(BG)
		}
	}();
	bz.bdy = function(jV, vn) {
		if (!jV || jV.length == 0) return "";
		vn = vn || "/";
		var bP = [];
		for (var i = jV.length - 1; i >= 0; i--) {
			bP.unshift(jV[i].name)
		}
		return bP.join(vn)
	};
	bz.baR = function() {
		var Hv = function(mS, cZ, cW) {
			if (!mS || !mS.name) return "";
			if (!mS.id) return '<span class="' + cZ + '">' + bm.jS(mS.name) + "</span>";
			return "<a" + (cW ? ' target="_blank"' : "") + ' class="' + cZ + '" href="/artist?id=' + mS.id + '" hidefocus="true">' + bm.jS(mS.name) + "</a>"
		};
		return function(jV, bB, vn, cW) {
			if (!jV || !jV.length) return "";
			vn = vn || "/";
			bB = bB || "";
			var jl = [];
			for (var i = 0, bn = [], vo = [], fZ; i < jV.length; ++i) {
				jl.push(jV[i].name);
				if (!jV[i] || jV[i].id <= 0) {
					vo.push(jV[i]);
					continue
				}
				if (bm.es(bB)) {
					fZ = bB(jV[i])
				} else {
					fZ = Hv(jV[i], bB, cW)
				}!!fZ && bn.push(fZ)
			}
			for (var i = 0, fZ; i < vo.length; ++i) {
				if (bm.es(bB)) {
					fZ = bB(vo[i])
				} else {
					fZ = Hv(vo[i], bB, cW)
				}!!fZ && bn.push(fZ)
			}
			return '<span title="' + jl.join(vn) + '">' + bn.join(vn) + "</span>"
		}
	}();
	bz.PA = function(mq) {
		return !!mq && /^[0-9]{11}$/.test(mq)
	};
	bz.baS = function(mq) {
		if (!bz.PA(mq)) return mq;
		return mq.substring(0, 3) + "****" + mq.substr(7)
	};
	bz.vj = function() {
		var cA = /^\s+$/g;
		return function(fg) {
			return !fg || cA.test(fg)
		}
	}();
	bz.BD = function() {
		var BC = /[^\x00-\xfff]/g;
		return function(fg) {
			var Ne = fg.match(BC) || [],
				fy = Ne.length;
			return fg.length + fy
		}
	}();
	bz.BB = function() {
		var BC = /[^\x00-\xfff]/;
		return function(fg, gG) {
			for (var i = 0, len = fg.length; i < len && gG > 0; i++) {
				if (BC.test(fg.charAt(i))) {
					gG -= 2;
					if (gG < 0) {
						break
					}
				} else {
					gG -= 1
				}
			}
			return fg.substring(0, i)
		}
	}();
	bz.BS = function(fg, gG, oO) {
		gG = gG || 10;
		oO = oO || nej.p.eV.engine == "trident" && parseInt(nej.p.eV.release) < 5;
		if (oO && bz.BD(fg) > gG) {
			return bz.BB(fg, gG) + "..."
		} else {
			return fg
		}
	};
	bz.QX = function(bl) {
		return bl === document.activeElement && (!document.hasFocus || document.hasFocus()) && !!(bl.type || bl.href || ~bl.tabIndex)
	};
	bz.baP = function(bi, cW) {
		if (!bi || !cW) return !0;
		var bl, bv = bi.type.toLowerCase();
		if (bv == "mouseout") {
			bl = bi.relatedTarget || bi.toElement
		} else if (bv == "mouseover") {
			bl = bi.relatedTarget || bi.fromElement
		}
		return !bl || bl !== cW && !bz.Mw(cW, bl)
	};
	bz.JR = function() {
		bF = {};
		return function(bl, cO) {
			var bt = bg.gl(bl),
				bba = "hover-" + bt;
			if (!cO || !bt || !!bF[bba]) return;
			bF[bba] = !0;
			bo.bW(bt, "mouseover", function() {
				var BA = bg.cw(bl, "hshow") || [];
				var By = bg.cw(bl, "icn-dislike") || [];
				bg.cu(bt, "z-hover");
				bg.co(BA[0], "display", "block");
				bg.co(By[0], "display", "block")
			});
			bo.bW(bt, "mouseout", function() {
				var BA = bg.cw(bl, "hshow") || [];
				var By = bg.cw(bl, "icn-dislike") || [];
				bg.cr(bt, "z-hover");
				bg.co(BA[0], "display", "none");
				bg.co(By[0], "display", "none")
			})
		}
	}();
	bz.VQ = function() {
		var bQ = {
			r: /\(|\)|\[|\]|\{|\}|\*|\+|\^|\$|\?|\!|\\|\||\./gi,
			"(": "\\(",
			")": "\\)",
			"[": "\\[",
			"]": "\\]",
			"{": "\\{",
			"}": "\\}",
			"*": "\\*",
			"+": "\\+",
			"^": "\\^",
			$: "\\$",
			"?": "\\?",
			"!": "\\!",
			"\\": "\\\\",
			"|": "\\|",
			".": "\\."
		};
		return function(fg) {
			return bm.rl(bQ, fg)
		}
	}();
	bz.Bw = function(dg) {
		if (bm.Jt(dg)) dg = dg.getTime();
		var ly = new Date,
			mU = ly.getTime() - dg;
		if (mU <= 6e4) return "刚刚";
		var uY = ly.getHours() * 36e5 + ly.getMinutes() * 6e4 + ly.getSeconds() * 1e3;
		if (mU <= uY) {
			if (mU < 36e5) {
				var NR = Math.floor(mU / 6e4);
				return NR + "分钟前"
			}
			return bm.rk(dg, "HH:mm")
		} else {
			if (mU < uY + 864e5) {
				return "昨天" + bm.rk(dg, "HH:mm")
			} else {
				var OG = ly.getFullYear(),
					OH = new Date(OG, 0, 1);
				var uY = ly.getTime() - OH.getTime();
				if (mU < uY) {
					return bm.rk(dg, "M月d日 HH:mm")
				}
				return bm.rk(dg, "yyyy年M月d日")
			}
		}
	};
	bz.baN = function() {
		var cA = /\{(.*?)\}/gi;
		return function(hZ, bk) {
			return (hZ || "").replace(cA, function($1, $2) {
				var bA = bk[$2];
				return bA == null ? $1 : bA
			})
		}
	}();
	bz.hm = function() {
		var bO = Array.prototype.slice.call(arguments, 0),
			hZ = bO.shift();
		if (hZ) {
			return hZ.replace(/{(\d+)}/g, function($1, $2) {
				return $2 < bO.length ? bO[$2] : $1
			})
		}
		return ""
	};
	bz.baM = function(bn, cZ, fu) {
		return "";
		fu = fu || " - ";
		if (bn && bn.length) {
			return fu + (!!cZ ? '<span class="' + cZ + '">' + bn[0] + "</span>" : bn[0])
		}
		return ""
	};
	bz.Qi = function() {
		if (window.getSelection) {
			var uX = window.getSelection();
			if (uX && uX.focusNode && uX.focusNode.tagName) {
				var BZ = bg.gL(uX.focusNode);
				for (var i = 0, jJ; i < BZ.length; ++i) {
					jJ = BZ[i].tagName;
					if (!jJ) continue;
					jJ = jJ.toLowerCase();
					if (jJ == "textarea" || jJ == "input") return !0
				}
			}
		} else if (document.selection) {
			var Cg = document.selection.createRange();
			if (Cg) {
				var bl = Cg.parentElement();
				if (bl && bl.tagName) {
					var jJ = bl.tagName.toLowerCase();
					if (jJ == "textarea" || jJ == "input") return !0
				}
			}
		}
		return !1
	};
	bz.baK = function(jN) {
		if (/^[A-Z]\:\\/i.test(jN)) {
			jN = jN.split("\\")
		} else {
			jN = jN.split("/")
		}
		jN = jN[jN.length - 1];
		return jN
	};
	bz.baJ = function() {
		var lM = [13, 17, 34, 19, 18, 21];
		return function(bt) {
			var bP = (bt || "").split("_");
			return {
				type: lM[bP[2]] || -1,
				id: bP[3] || ""
			}
		}
	}();
	bz.Ci = function(mF) {
		if (4 == mF.userType) {
			return '<sup class="icn u-icn2 u-icn2-music2"></sup>'
		} else if (mF.authStatus == 1) {
			return '<sup class="u-icn u-icn-1"></sup>'
		} else if (mF.expertTags && mF.expertTags.length) {
			return '<sup class="u-icn u-icn-84"></sup>'
		}
	};
	bz.baI = function(dP) {
		dP += "";
		if (dP) {
			return dP.substr(0, 3) + "****" + dP.substr(dP.length - 4)
		}
	};
	bz.baH = function(bfo, cR) {
		return (bfo % cR + cR) % cR
	};
	bz.baG = function() {
		var lM = ["playlist", "program", "event", "album", "song", "mv", "topic"];
		return function(bt) {
			var bP = (bt || "").split("_"),
				bw = {
					type: lM[bP[2]] || -1,
					id: bP[3] || ""
				};
			if (bw.type == "event") {
				bw.uid = bP[4] || "";
				return "/" + bw.type + "?id=" + bw.id + "&uid=" + bw.uid
			}
			return "/" + bw.type + "?id=" + bw.id
		}
	}();
	bz.baE = function() {
		var lM = ["歌单", "电台节目", "动态", "专辑", "单曲", "MV", "专栏文章"];
		return function(bt) {
			var bP = (bt || "").split("_");
			return lM[bP[2]] || "资源"
		}
	}();
	bz.baD = function(cn) {
		var qs = cn.length > 0 ? cn.substring(1) : "",
			args = {},
			items = qs.length ? qs.split("&") : [],
			item = null,
			name = null,
			value = null,
			i = 0,
			len = items.length;
		for (i = 0; i < len; i++) {
			item = items[i].split("=");
			name = decodeURIComponent(item[0]);
			value = decodeURIComponent(item[1]);
			if (name.length) {
				args[name] = value
			}
		}
		return args
	};
	bz.baC = function(jQ, rv) {
		var uQ = 0,
			ey = new Array;
		bm.bfq(jQ, function(bC, bfo) {
			var Bi = bg.hw("img");
			Bi.src = bC;
			bo.bW(Bi, "load", function(bfo, bi) {
				ey[bfo] = 1;
				uQ++;
				if (uQ == jQ.length) rv(jQ, ey)
			}.bq(this, bfo));
			bo.bW(Bi, "error", function(bi, bfo) {
				ey[bfo] = 0;
				uQ++;
				if (uQ == jQ.length) rv(jQ, ey)
			}.bq(this, bfo))
		})
	};
	bz.baz = function(bn, ek) {
		var bw = [];
		bm.bfq(bn, function(bfp) {
			bw.push(ek(bfp))
		});
		return bw
	};
	bz.bay = function(bU) {
		return bm.jS((bU || "").replace(/\s{2,}/g, " ").trim())
	};
	bz.bax = function(cB) {
		if (cB.transNames && cB.transNames.length) {
			return cB.transNames[0]
		} else if (cB.alias && cB.alias.length) {
			return cB.alias[0]
		}
	};
	bz.bav = function(qC) {
		if (qC) {
			return qC.replace(/\n{2,}/g, "<br/><br/>").replace(/\n/g, "<br/>").replace(/(<br\/?>){2,}/g, "<br/><br/>")
		}
	};
	bz.bau = function(bl) {
		var bl = bg.bL(bl),
			dR = bl && bl.getElementsByTagName("textarea")[0],
			bba = bg.bH(bl, "key"),
			PR = bD.lB.nu();
		if (!(bl && dR && bba)) return;
		PR.mP(bba, JSON.parse(dR.value));
		bl.innerHTML = "";
		return bba
	};
	bz.bat = function(jy) {
		if (!jy.onbeforelistload) {
			jy.onbeforelistload = function(bi) {
				bi.value = '<div class="u-load s-fc4"><i class="icn"></i> 加载中...</div>'
			}
		}
		if (!jy.onemptylist) {
			jy.onemptylist = function(bi) {
				bi.value = '<div class="n-nmusic"><h3 class="f-ff2"><i class="u-icn u-icn-21"></i>' + (jy.emptyMsg || "暂时还没有数据") + "</h3></div>"
			}
		}
		return jy
	};
	bz.qW = function(ei) {
		if (!ei) return null;
		var rn = {
			album: ei.al,
			alias: ei.alia || ei.ala || [],
			artists: ei.ar || [],
			commentThreadId: "R_SO_4_" + ei.id,
			copyrightId: ei.cp || 0,
			duration: ei.dt || 0,
			id: ei.id,
			mvid: ei.mv || 0,
			name: ei.name || "",
			cd: ei.cd,
			position: ei.no || 0,
			ringtone: ei.rt,
			rtUrl: ei.rtUrl,
			status: ei.st || 0,
			pstatus: ei.pst || 0,
			fee: ei.fee || 0,
			version: ei.v || 0,
			eq: ei.eq,
			songType: ei.t || 0,
			mst: ei.mst,
			score: ei.pop || 0,
			ftype: ei.ftype,
			rtUrls: ei.rtUrls,
			transNames: ei.tns,
			privilege: ei.privilege,
			lyrics: ei.lyrics
		};
		return rn
	};
	bz.AY = function() {
		var bl = bg.nt('<div class="u-mask u-mask-light"></div>' + '<div class="m-opentip">' + '<div class="lay">' + '<div class="note">' + "<h3>分享打不开？</h3>" + '<p>请点击右上角<br>选择<span class="s-fc5">“分享到...”</span></p>' + "</div></div></div>");
		document.body.appendChild(bl);
		bo.bW(bl, "click", function(bi) {
			bo.ep(bi);
			bg.gC(bl)
		})
	};
	bz.bas = function(ef) {
		if (ef < 1e5) {
			return ef
		} else {
			return Math.floor(ef / 1e4) + "万"
		}
	};
	bz.bar = function(ef, dR) {
		return "<i>" + (ef ? "(" + ef + ")" : dR) + "</i>"
	};
	bz.bdG = function(bv, bbf) {
		var bf = {};
		if (!bm.jq(bbf)) {
			return bf
		}
		switch (parseInt(bv)) {
			case 17:
				bf.title = bbf.name;
				bf.author = (bbf.radio || []).name;
				bf.picUrl = bbf.coverUrl;
				bf.category = bbf.radio.category;
				break;
			case 19:
				bf.title = bbf.name;
				bf.author = bz.bdy(bbf.artists);
				bf.authors = bz.bdy(bbf.artists, " / ");
				bf.picUrl = bbf.picUrl;
				break;
			case 13:
				bf.title = bbf.name;
				bf.author = (bbf.creator || []).nickname;
				bf.picUrl = bbf.coverImgUrl;
				break;
			case 18:
				bf.title = bbf.name;
				bf.author = bz.bdy(bbf.artists);
				bf.picUrl = (bbf.album || []).picUrl;
				break;
			case 20:
				bf.title = bbf.name;
				bf.author = "";
				bf.picUrl = bbf.img1v1Url;
				break;
			case 21:
				bf.title = bbf.name;
				bf.author = bbf.artistName;
				bf.authors = bz.bdy(bbf.artists, " / ");
				bf.picUrl = bbf.newCover || bbf.cover;
				break;
			case 70:
				bf.title = bbf.name;
				bf.author = (bbf.dj || []).nickname;
				bf.picUrl = bbf.picUrl;
				bf.category = bbf.category;
				break;
			default:
				break
		}
		return bf
	}
})();
(function() {
	var bh = NEJ.P,
		bg = bh("nej.e"),
		bo = bh("nej.v"),
		bR = bh("nej.ut"),
		cV = bh("nm.m.mob"),
		bj, cf;
	cV.iZ = NEJ.C();
	bj = cV.iZ.cg(bR.dE);
	bj.cI = function() {
		this.dv();
		this.fk()
	};
	bj.fk = function() {
		this.eO([
			[document, "click", this.ql.bq(this)]
		])
	};
	bj.ql = function() {
		var cD = typeof GFrom === "undefined" ? "" : GFrom,
			eA = typeof GClient === "undefined" ? "" : GClient;
		var AR = /^http[s]?\:\/\/(.*?)\/#/,
			AQ = /^http[s]?\:\/\/(.*?)\/m\//,
			AP = "orpheus://";
		var AO = function(du) {
			return /^orpheus\:\/\//.test(du) || eA == "micromessenger" && (/^https\:\/\//.test(du) || du.indexOf("/api/android/download") >= 0)
		};
		var AL = function() {
			var hv = document.getElementById("mask"),
				hM = document.getElementById("opentip");
			if (hv) hv.style.display = "";
			if (hM) hM.style.display = ""
		};
		return function(bi) {
			var bl = bo.cK(bi);
			if (bl.href && !AR.test(bl.href)) {
				var du = bl.href;
				if (bg.bH(bl, "link") == "app") {
					du = du.replace(AQ, AP);
					if (cD) {
						du = du + (du.indexOf("?") >= 0 ? "&" : "?") + "from=" + cD
					}
				}
				bo.fw(bi);
				if (AO(du)) {
					AL()
				} else {
					location.href = du
				}
			}
			var cC = bg.bH(bl, "action")
		}
	}();
	if (bg.bH(document.body, "module") == "self") {
		window.GModule = cV.iZ.bZ()
	}
})()