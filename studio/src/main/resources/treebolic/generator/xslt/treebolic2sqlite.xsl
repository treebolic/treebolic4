<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!--TREEBOLIC 2 SQL- 2008/05/01 (C) 2008 Author: Bernard Bou -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no" encoding="UTF8" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<xsl:text>
--nodes--
CREATE TABLE IF NOT EXISTS "nodes" (
	"n" UNSIGNED INT,
	"id" VARCHAR(64) NOT NULL,
	"label" VARCHAR(64),
	"content" TEXT,
	"backcolor" VARCHAR(6),
	"forecolor" VARCHAR(6),
	"image" VARCHAR(255),
	"link" VARCHAR(255),
	"target" VARCHAR(32),
	"weight" DOUBLE,
	"mountpoint" VARCHAR(255),
	"mountnow" BOOLEAN,
	PRIMARY KEY("n"));
--edges--
CREATE TABLE IF NOT EXISTS "edges" (
	"n" UNSIGNED INT,
	"istree" BOOLEAN, 
	"from" VARCHAR(64) NOT NULL,
	"to" VARCHAR(64) NOT NULL,
	"label" VARCHAR(64),
	"image" VARCHAR(255),
	"color" VARCHAR(6),
	"hidden" BOOLEAN,
	"stroke" VARCHAR(5),
	"fromterminator" VARCHAR(2),
	"toterminator" VARCHAR(2),
	PRIMARY KEY("n"));
--settings--
CREATE TABLE IF NOT EXISTS "settings" (
	"n" UNSIGNED INT,	
	"backimage" VARCHAR(255),
	"backcolor" VARCHAR(6),
	"forecolor" VARCHAR(6),
	"fontface" VARCHAR(64),
	"fontsize" INT,
	"scalefonts" BOOLEAN,
	"fontscaler" VARCHAR(64),
	"scaleimages" BOOLEAN,
	"imagescaler" VARCHAR(64),
	"orientation" VARCHAR(6),
	"expansion" DOUBLE,
	"sweep" DOUBLE,
	"preserveorientation" BOOLEAN,
	"hastoolbar" BOOLEAN,
	"hasstatusbar" BOOLEAN,
	"haspopupmenu" BOOLEAN,
	"hastooltip" BOOLEAN,
	"tooltipdisplayscontent" BOOLEAN,
	"focusonhover" BOOLEAN,
	"focus" VARCHAR(64),
	"xmoveto" DOUBLE,
	"ymoveto" DOUBLE,
	"xshift" DOUBLE,
	"yshift" DOUBLE,
	"nodebackcolor" VARCHAR(6),
	"nodeforecolor" VARCHAR(6),
	"nodeborder" BOOLEAN,
	"nodeellipsize" BOOLEAN,
	"nodeimage" VARCHAR(255),
	"treeedgecolor" VARCHAR(6),
	"treeedgehidden" BOOLEAN,
	"treeedgestroke" VARCHAR(5),
	"treeedgefromterminator" VARCHAR(2),
	"treeedgetoterminator" VARCHAR(2),
	"treeedgeimage" VARCHAR(255),
	"edgearc" BOOLEAN,
	"edgecolor" VARCHAR(6),
	"edgehidden" BOOLEAN,
	"edgestroke" VARCHAR(5),
	"edgefromterminator" VARCHAR(2),
	"edgetoterminator" VARCHAR(2),
	"edgeimage" VARCHAR(255),
	PRIMARY KEY("n"));
--settings--
CREATE TABLE IF NOT EXISTS "menu" (
	"n" UNSIGNED INT,	
	"menuid" INT,
	"action" VARCHAR(6),
	"label" VARCHAR(255),
	"target" VARCHAR(255),
	"scope"VARCHAR(7),
	"mode"VARCHAR(10),
	"link" VARCHAR(255),
	PRIMARY KEY("n"));&#xA;</xsl:text>
		<xsl:apply-templates select="./treebolic" />
		<xsl:text>--index--&#xA;</xsl:text>
		<xsl:text>CREATE UNIQUE INDEX unq_id ON "nodes" ("id");&#xA;</xsl:text>
	</xsl:template>

	<xsl:template match="treebolic">
		<xsl:apply-templates select="./tree" />
		<xsl:text>--settings data--&#xA;</xsl:text>
		<xsl:text>INSERT INTO "settings" (
	"backimage",
	"backcolor",
	"forecolor",
	"fontface",
	"fontsize",
	"scalefonts",
	"fontscaler",
	"scaleimages",
	"imagescaler",
	"orientation",
	"expansion",
	"sweep",
	"preserveorientation",
	"hastoolbar",
	"hasstatusbar",
	"haspopupmenu",
	"hastooltip",
	"tooltipdisplayscontent",
	"focusonhover",
	"focus",
	"xmoveto",
	"ymoveto",
	"xshift",
	"yshift",
	"nodebackcolor",
	"nodeforecolor",
	"nodeborder",
	"nodeellipsize",
	"nodeimage",
	"treeedgecolor",
	"treeedgehidden",
	"treeedgestroke",
	"treeedgefromterminator",
	"treeedgetoterminator",
	"treeedgeimage",
	"edgearc",
	"edgecolor",
	"edgehidden",
	"edgestroke",
	"edgefromterminator",
	"edgetoterminator",
	"edgeimage"
) VALUES (</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/img/@src" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@backcolor" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@forecolor" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@fontface" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@fontsize" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@scalefonts" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@fontscaler" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@scaleimages" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@imagescaler" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@orientation" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@expansion" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@sweep" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/@preserve-orientation" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>

		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@toolbar" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@statusbar" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@popupmenu" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@tooltip" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@tooltip-displays-content" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@focus-on-hover" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@focus" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@xmoveto" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@ymoveto" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@xshift" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@yshift" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>

		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/@backcolor" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/@forecolor" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/@border" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/@ellipsize" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/img/@src" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>

		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/default.treeedge/@color" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/default.treeedge/@hidden" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/default.treeedge/@stroke" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/default.treeedge/@fromterminator" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/default.treeedge/@toterminator" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/nodes/default.treeedge/img/@src" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>

		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/@arcs" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/default.edge/@color" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/default.edge/@hidden" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/default.edge/@stroke" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/default.edge/@fromterminator" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/default.edge/@toterminator" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./tree/edges/default.edge/img/@src" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>);
</xsl:text>
		<xsl:apply-templates select="//tools" />
	</xsl:template>

	<xsl:template match="tree">
		<xsl:text>--nodes data--
</xsl:text>
		<xsl:apply-templates select="./nodes" />
		<xsl:text>--edges data--
</xsl:text>
		<xsl:apply-templates select="./edges" />
	</xsl:template>

	<xsl:template match="nodes">
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="edges">
		<xsl:apply-templates select="./edge" />
	</xsl:template>

	<xsl:template match="node">
		<xsl:text>INSERT INTO "nodes" ("id","label","content","backcolor","forecolor","image","link","target","weight","mountnow","mountpoint") VALUES (</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@id" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./label/text()" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./content/text()" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@backcolor" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@forecolor" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./img/@src" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./a/@href" disable-output-escaping="yes" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./a/@target" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@weight" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./mountpoint/@now" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./mountpoint/a/@href" disable-output-escaping="yes" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>);
</xsl:text>
		<xsl:if test="boolean(parent::node)">
			<xsl:text>INSERT INTO "edges" ("istree","from","to","label","image","color","hidden","stroke","fromterminator","toterminator") VALUES (</xsl:text>
			<xsl:text>1,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="parent::node/@id" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./@id" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/label/text()" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/img/@src" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/@color" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="uvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/@hidden" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/@stroke" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/@fromterminator" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./treeedge/@toterminator" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>);
</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="edge">
		<xsl:text>INSERT INTO "edges" ("istree","from","to","label","image","color","hidden","stroke","fromterminator","toterminator") VALUES (</xsl:text>
		<xsl:text>0,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@from" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@to" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./label/text()" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./img/@src" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@color" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="uvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@hidden" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@stroke" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@fromterminator" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="qvalue">
			<xsl:with-param name="x">
				<xsl:value-of select="./@toterminator" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>);
</xsl:text>
	</xsl:template>

	<xsl:template match="tools">
		<xsl:text>--menu data--
</xsl:text>
		<xsl:for-each select=".//menuitem">
			<xsl:text>INSERT INTO "menu" ("menuid","action","label","target","scope","mode","link") VALUES (</xsl:text>
			<xsl:text>0,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./@action" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./label/text()" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./@match-target" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./@match-scope" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./@match-mode" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="qvalue">
				<xsl:with-param name="x">
					<xsl:value-of select="./a/@href" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>);
</xsl:text>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="qvalue">
		<xsl:param name="x" />
		<xsl:choose>
			<xsl:when test="$x=''">
				<xsl:text>NULL</xsl:text>
			</xsl:when>
			<xsl:when test="$x='true'">
				<xsl:text>1</xsl:text>
			</xsl:when>
			<xsl:when test="$x='false'">
				<xsl:text>0</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>'</xsl:text>
				<xsl:value-of select="$x" />
				<xsl:text>'</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="uvalue">
		<xsl:param name="x" />
		<xsl:choose>
			<xsl:when test="$x=''">
				<xsl:text>NULL</xsl:text>
			</xsl:when>
			<xsl:when test="$x='true'">
				<xsl:text>1</xsl:text>
			</xsl:when>
			<xsl:when test="$x='false'">
				<xsl:text>0</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$x" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
