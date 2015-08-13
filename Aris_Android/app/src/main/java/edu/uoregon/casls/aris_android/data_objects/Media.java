package edu.uoregon.casls.aris_android.data_objects;

import java.net.URL;

/**
 * Created by smorison on 8/13/15.
 */
public class Media {
	public long media_id;
	public long game_id;
	public long user_id; //??
	public String name;
	public String fileName;

	public URL localURL; // local file URL?
	public URL remoteURL; // are both url and thumb_url always the same?
	public URL localThumbURL;
	public URL remoteThumbURL; // are both url and thumb_url always the same?

	// From iOS. Not sure how to replicate in android.
//	@property (nonatomic, strong) NSData *data;
//	@property (nonatomic, strong) NSData *thumb;

//	public fileExtension(URL url)
//	{
//		if(mediaCD.remoteURL) return [mediaCD.remoteURL pathExtension];
//		return [mediaCD.localURL pathExtension];
//	}
//
//	// return general media file type
//	public String type() {
//
//			NSString *ext = [[self fileExtension] lowercaseString];
//			if([ext isEqualToString:@"jpg"]  ||
//			[ext isEqualToString:@"jpeg"] ||
//			[ext isEqualToString:@"png"]  ||
//			[ext isEqualToString:@"gif"])
//			{
//				return @"IMAGE";
//			}
//			else if([ext isEqualToString:@"mov"] ||
//			[ext isEqualToString:@"avi"] ||
//			[ext isEqualToString:@"3gp"] ||
//			[ext isEqualToString:@"m4v"] ||
//			[ext isEqualToString:@"mp4"])
//			{
//				return @"VIDEO";
//			}
//			else if([ext isEqualToString:@"mp3"] ||
//			[ext isEqualToString:@"wav"] ||
//			[ext isEqualToString:@"m4a"] ||
//			[ext isEqualToString:@"ogg"] ||
//			[ext isEqualToString:@"caf"])
//			{
//				return @"AUDIO";
//			}
//			else return @"";
//
//
//	}
}
