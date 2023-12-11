<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!-- WORDNET 2 TREEBOLIC - 18/12/2006 - 13:0 (C) 2006 Author: Bernard Bou -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="no" />

	<xsl:template match="/">
		<xsl:element name="treebolic">
			<xsl:attribute name="toolbar">false</xsl:attribute>
			<xsl:attribute name="statusbar">true</xsl:attribute>
			<xsl:attribute name="tooltip">true</xsl:attribute>
			<xsl:attribute name="popupmenu">true</xsl:attribute>
			<xsl:element name="tree">
				<xsl:attribute name="backcolor">F3F1E2</xsl:attribute>
				<xsl:attribute name="forecolor">0000FF</xsl:attribute>
				<xsl:attribute name="orientation">radial</xsl:attribute>
				<xsl:attribute name="preserve-orientation">true</xsl:attribute>
				<xsl:element name="nodes">
					<xsl:attribute name="backcolor">DEDEFF</xsl:attribute>
					<xsl:attribute name="forecolor">0000FF</xsl:attribute>
					<xsl:element name="img">
						<xsl:attribute name="src">item.gif</xsl:attribute>
					</xsl:element>
					<xsl:apply-templates select="//wordnet" />
				</xsl:element>
				<xsl:element name="edges">
					<xsl:attribute name="treecolor">0000FF</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<tools>
				<menu>
					<menuitem action="focus">
						<label>Focus</label>
					</menuitem>
					<menuitem action="search" match-target="$e" match-scope="label" match-mode="includes">
						<label>Search (name includes $e)</label>
					</menuitem>
					<menuitem action="search" match-target="$e" match-scope="content" match-mode="includes">
						<label>Search (content includes $e)</label>
					</menuitem>
					<menuitem action="search" match-target="W$e" match-scope="id" match-mode="equals">
						<label>Search (word id equals $e)</label>
					</menuitem>
					<menuitem action="search" match-target="S$e" match-scope="id" match-mode="equals">
						<label>Search (synset id equals $e)</label>
					</menuitem>
				</menu>
			</tools>
		</xsl:element>
	</xsl:template>

	<xsl:template match="wordnet">
		<xsl:element name="node">
			<xsl:attribute name="id">root</xsl:attribute>
			<xsl:attribute name="backcolor">0000FF</xsl:attribute>
			<xsl:attribute name="forecolor">FFFFFF</xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="normalize-space(text())" />
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">focus.gif</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./pos" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="pos">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<xsl:attribute name="backcolor">FFFFFF</xsl:attribute>
			<xsl:attribute name="forecolor">0000FF</xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="@name" />
			</xsl:element>
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">tf</xsl:attribute>
				<xsl:attribute name="stroke">solid</xsl:attribute>
				<xsl:attribute name="color">0000FF</xsl:attribute>
				<xsl:element name="label">
					pos
				</xsl:element>
				<xsl:element name="img">
					<xsl:attribute name="src">pos.gif</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">pos.gif</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./category" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="category">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<xsl:attribute name="backcolor">FFFFFF</xsl:attribute>
			<xsl:attribute name="forecolor">000000</xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="@name" />
			</xsl:element>
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">tf</xsl:attribute>
				<xsl:attribute name="stroke">solid</xsl:attribute>
				<xsl:attribute name="color">000000</xsl:attribute>
				<xsl:element name="label">
					category
				</xsl:element>
				<xsl:element name="img">
					<xsl:attribute name="src">category.gif</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">category.gif</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./sense" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="sense">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<xsl:attribute name="backcolor">800080</xsl:attribute>
			<xsl:attribute name="forecolor">FFFFFF</xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="@number" />
			</xsl:element>
			<xsl:element name="content">
				<xsl:value-of select="normalize-space(synset/definition/text())" />
			</xsl:element>
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">tf</xsl:attribute>
				<xsl:attribute name="color">800080</xsl:attribute>
				<xsl:element name="label">
					sense
				</xsl:element>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">sense.gif</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./synset" />
			<xsl:apply-templates select="./links" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="synset">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="concat('S',./@synset-id)" /></xsl:attribute>
			<!--<xsl:element name="label">=</xsl:element> -->
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">cf</xsl:attribute>
				<xsl:attribute name="stroke">dash</xsl:attribute>
				<xsl:attribute name="color">606060</xsl:attribute>
				<xsl:element name="label">
					synset
				</xsl:element>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">synset.gif</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./word" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="word">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="concat('W',./@word-id)" /></xsl:attribute>
			<xsl:attribute name="backcolor">F3F1E2</xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="normalize-space(text())" />
			</xsl:element>
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">tf</xsl:attribute>
				<xsl:attribute name="stroke">dash</xsl:attribute>
				<xsl:attribute name="color">606060</xsl:attribute>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">item.gif</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="links">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<!--<xsl:element name="label"><xsl:value-of select="name()" /></xsl:element> -->
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">cf</xsl:attribute>
				<xsl:attribute name="stroke">solid</xsl:attribute>
				<xsl:attribute name="color">FF0000</xsl:attribute>
				<xsl:element name="label">
					links
				</xsl:element>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">links.gif</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>

	<xsl:template
		match="hypernym|hyponym|hypernym_instance|hyponym_instance|holonym_part|meronym_part|holonym_member|meronym_member|holonym_substance|meronym_substance|entails|causes">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="name()" />
			</xsl:element>
			<xsl:element name="treeedge">
				<xsl:attribute name="fromterminator">tf</xsl:attribute>
				<xsl:attribute name="stroke">solid</xsl:attribute>
				<xsl:attribute name="color">FF0000</xsl:attribute>
				<xsl:element name="label">
					<xsl:value-of select="name()" />
				</xsl:element>
				<xsl:element name="img">
					<xsl:attribute name="src">
         <xsl:value-of select="concat(name(),'.gif')" />
       </xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">
     <xsl:value-of select="concat(name(),'.gif')" />
    </xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="also|similar|derivation|domain">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="name()" />
			</xsl:element>
			<xsl:element name="img">
				<xsl:attribute name="src">
     <xsl:value-of select="concat(name(),'.gif')" />
    </xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="*">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="generate-id()" /></xsl:attribute>
			<xsl:element name="label">
				<xsl:value-of select="name()" />
			</xsl:element>
			<xsl:apply-templates select="./*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*">
	</xsl:template>

	<xsl:template match="text()">
	</xsl:template>

</xsl:stylesheet>
